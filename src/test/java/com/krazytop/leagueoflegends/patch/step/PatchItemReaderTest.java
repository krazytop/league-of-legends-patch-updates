package com.krazytop.leagueoflegends.patch.step;

import com.krazytop.leagueoflegends.batch.model.PatchMetadata;
import com.krazytop.leagueoflegends.batch.step.PatchItemReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatchItemReaderTest {

    @InjectMocks
    private PatchItemReader reader;

    @Mock
    private PatchMetadata patchMetadataMock;

    private List<PatchMetadata> patchesMetadata;
    private Field patchesMetadataField;

    @BeforeEach
    void setUp() throws NoSuchFieldException {
        patchesMetadataField = PatchItemReader.class.getDeclaredField("newPatchesMetadata");
        patchesMetadataField.setAccessible(true);
        patchesMetadata = List.of(patchMetadataMock);
    }

    @Test
    void beforeStep() throws IllegalAccessException {
        ExecutionContext executionContext = spy(ExecutionContext.class);
        executionContext.put("newPatchesMetadata", patchesMetadata);

        JobExecution jobExecution = mock(JobExecution.class);
        when(jobExecution.getExecutionContext()).thenReturn(executionContext);

        StepExecution stepExecution = mock(StepExecution.class);
        when(stepExecution.getJobExecution()).thenReturn(jobExecution);

        reader.beforeStep(stepExecution);

        assertThat(patchesMetadataField.get(reader)).isEqualTo(patchesMetadata);
    }

    @Test
    void read_StillElements() throws IllegalAccessException {
        patchesMetadataField.set(reader, patchesMetadata);

        PatchMetadata patchMetadata = reader.read();

        assertThat(patchMetadata).isEqualTo(patchMetadataMock);
    }

    @Test
    void read_NoMoreElements() throws IllegalAccessException {
        patchesMetadataField.set(reader, List.of());

        PatchMetadata patchMetadata = reader.read();

        assertThat(patchMetadata).isNull();
    }
}