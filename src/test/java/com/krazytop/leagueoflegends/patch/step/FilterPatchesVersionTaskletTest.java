package com.krazytop.leagueoflegends.patch.step;

import com.krazytop.leagueoflegends.batch.model.PatchMetadata;
import com.krazytop.leagueoflegends.batch.step.FilterPatchesVersionTasklet;
import com.krazytop.leagueoflegends.entity.Metadata;
import com.krazytop.leagueoflegends.service.PatchService;
import com.krazytop.leagueoflegends.repository.MetadataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.*;
import java.util.stream.Stream;

import static com.krazytop.leagueoflegends.service.PatchService.SUPPORTED_LANGUAGES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilterPatchesVersionTaskletTest {

    @InjectMocks
    private FilterPatchesVersionTasklet tasklet;

    @Mock
    private PatchService patchService;
    @Mock
    private MetadataRepository metadataRepository;

    @Test
    void execute() throws Exception {
        Set<String> allVersions = Set.of("14.1.1", "14.2.1", "14.3.1");
        Set<String> existingVersions = Set.of("14.1");

        Metadata metadata = new Metadata();
        metadata.setAllPatches(existingVersions);

        when(patchService.getAllVersions()).thenReturn(allVersions);
        when(metadataRepository.findMetadata()).thenReturn(Optional.of(metadata));

        StepContribution contribution = mock(StepContribution.class);
        ChunkContext chunkContext = mock(ChunkContext.class);
        StepExecution stepExecution = mock(StepExecution.class);
        JobExecution jobExecution = mock(JobExecution.class);
        ExecutionContext executionContext = new ExecutionContext();

        StepContext stepContext = mock(StepContext.class);
        when(chunkContext.getStepContext()).thenReturn(stepContext);
        when(stepContext.getStepExecution()).thenReturn(stepExecution);
        when(stepExecution.getJobExecution()).thenReturn(jobExecution);
        when(jobExecution.getExecutionContext()).thenReturn(executionContext);

        RepeatStatus status = tasklet.execute(contribution, chunkContext);

        assertThat(status).isEqualTo(RepeatStatus.FINISHED);
        assertThat(executionContext.containsKey("newPatchesMetadata")).isTrue();

        List<PatchMetadata> results = (List<PatchMetadata>) executionContext.get("newPatchesMetadata");

        int expectedCount = 2 * SUPPORTED_LANGUAGES.size();
        assertThat(results).hasSize(expectedCount);
        List<PatchMetadata> expectedPatchesMetadata = Stream.of("14.2.1", "14.3.1")
                .flatMap(version -> SUPPORTED_LANGUAGES.stream()
                        .map(lang -> new PatchMetadata(version, lang)))
                .toList();
        assertThat(results).containsExactlyInAnyOrderElementsOf(expectedPatchesMetadata);
        verify(metadataRepository).findMetadata();
    }
}
