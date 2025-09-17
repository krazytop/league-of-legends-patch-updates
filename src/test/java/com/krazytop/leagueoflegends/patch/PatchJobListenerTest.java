package com.krazytop.leagueoflegends.patch;

import com.krazytop.leagueoflegends.batch.PatchJobListener;
import com.krazytop.leagueoflegends.batch.model.PatchMetadata;
import com.krazytop.leagueoflegends.entity.Metadata;
import com.krazytop.leagueoflegends.entity.Patch;
import com.krazytop.leagueoflegends.repository.MetadataRepository;
import com.krazytop.leagueoflegends.repository.PatchRepository;
import com.krazytop.leagueoflegends.service.PatchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.item.ExecutionContext;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatchJobListenerTest {

    @InjectMocks
    private PatchJobListener patchJobListener;

    @Mock
    private MetadataRepository metadataRepository;
    @Mock
    private PatchRepository patchRepository;
    @Mock
    private PatchService patchService;
    @Mock
    private JobExecution jobExecution;
    @Mock
    private ExecutionContext executionContext;
    @Mock
    private List<PatchMetadata> newPatchMetadata;

    @Test
    void testAfterJob_withCompletedStatus_andNewPatchesMetadata_shouldUpdateMetadata_fromService() throws IOException, URISyntaxException {
        Metadata metadata = new Metadata();

        when(jobExecution.getExecutionContext()).thenReturn(executionContext);
        when(executionContext.get("newPatchesMetadata")).thenReturn(newPatchMetadata);
        when(jobExecution.getStatus()).thenReturn(BatchStatus.COMPLETED);
        when(metadataRepository.findAll()).thenReturn(List.of(metadata));
        when(patchService.getCurrentPatchVersion()).thenReturn("15.1");

        patchJobListener.afterJob(jobExecution);

        assertThat(metadata.getCurrentPatch()).isEqualTo("15.1");
        assertThat(metadata.getCurrentSeason()).isEqualTo(15);
        verify(metadataRepository).save(metadata);
    }

    @Test
    void afterJob_Exception() throws IOException, URISyntaxException {
        Metadata metadata = new Metadata();
        Patch latestPatch = Patch.builder().patchId("15.1").season(15).build();

        when(jobExecution.getExecutionContext()).thenReturn(executionContext);
        when(executionContext.get("newPatchesMetadata")).thenReturn(newPatchMetadata);
        when(jobExecution.getStatus()).thenReturn(BatchStatus.COMPLETED);
        when(metadataRepository.findAll()).thenReturn(List.of(metadata));
        when(patchService.getCurrentPatchVersion()).thenThrow(new IOException());
        when(patchRepository.findLatestPatch()).thenReturn(latestPatch);

        patchJobListener.afterJob(jobExecution);

        assertThat(metadata.getCurrentPatch()).isEqualTo("15.1");
        assertThat(metadata.getCurrentSeason()).isEqualTo(15);
        verify(metadataRepository).save(metadata);
    }

    @Test
    void afterJob_NotCompleted() {
        when(jobExecution.getExecutionContext()).thenReturn(executionContext);
        when(executionContext.get("newPatchesMetadata")).thenReturn(newPatchMetadata);
        when(jobExecution.getStatus()).thenReturn(BatchStatus.FAILED);

        patchJobListener.afterJob(jobExecution);

        verifyNoInteractions(metadataRepository);
        verifyNoInteractions(patchRepository);
        verifyNoInteractions(patchService);
    }

    @Test
    void afterJob_NoNewPatches() {
        when(jobExecution.getExecutionContext()).thenReturn(executionContext);
        when(executionContext.get("newPatchesMetadata")).thenReturn(List.of());
        when(jobExecution.getStatus()).thenReturn(BatchStatus.COMPLETED);

        patchJobListener.afterJob(jobExecution);

        verifyNoInteractions(metadataRepository);
        verifyNoInteractions(patchRepository);
        verifyNoInteractions(patchService);
    }
}
