package com.krazytop.leagueoflegends.patch.listener;

import com.krazytop.leagueoflegends.batch.listener.PatchJobListener;
import com.krazytop.leagueoflegends.batch.model.PatchMetadata;
import com.krazytop.leagueoflegends.entity.Metadata;
import com.krazytop.leagueoflegends.repository.MetadataRepository;
import com.krazytop.leagueoflegends.service.PatchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
    private PatchService patchService;
    @Mock
    private JobExecution jobExecution;
    @Mock
    private ExecutionContext executionContext;
    @Mock
    private PatchMetadata newPatchMetadata;

    @Test
    void afterJob_NewPatches() throws IOException, URISyntaxException {
        when(jobExecution.getExecutionContext()).thenReturn(executionContext);
        when(executionContext.get("newPatchesMetadata")).thenReturn(List.of(newPatchMetadata));
        when(patchService.getCurrentPatchVersion()).thenReturn("15.1");

        patchJobListener.afterJob(jobExecution);

        ArgumentCaptor<Metadata> captor = ArgumentCaptor.forClass(Metadata.class);
        verify(metadataRepository).save(captor.capture());

        Metadata savedMetadata = captor.getValue();
        assertThat(savedMetadata.getCurrentPatch()).isEqualTo("15.1");
        assertThat(savedMetadata.getCurrentSeason()).isEqualTo(15);
    }

    @Test
    void afterJob_Exception() throws IOException, URISyntaxException {
        when(jobExecution.getExecutionContext()).thenReturn(executionContext);
        when(executionContext.get("newPatchesMetadata")).thenReturn(List.of(newPatchMetadata));
        when(patchService.getCurrentPatchVersion()).thenThrow(new IOException());

        patchJobListener.afterJob(jobExecution);

        verify(metadataRepository, never()).save(any());
    }

    @Test
    void afterJob_NoNewPatches() {
        when(jobExecution.getExecutionContext()).thenReturn(executionContext);
        when(executionContext.get("newPatchesMetadata")).thenReturn(List.of());

        patchJobListener.afterJob(jobExecution);

        verifyNoInteractions(metadataRepository);
        verifyNoInteractions(patchService);
    }
}
