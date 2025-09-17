package com.krazytop.leagueoflegends.patch.step;

import com.krazytop.leagueoflegends.batch.model.PatchMetadata;
import com.krazytop.leagueoflegends.batch.step.PatchItemProcessor;
import com.krazytop.leagueoflegends.entity.Patch;
import com.krazytop.leagueoflegends.service.PatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatchItemProcessorTest {

    @InjectMocks
    private PatchItemProcessor processor;

    @Mock
    private PatchService patchService;
    @Mock
    private Patch patchMock;

    @BeforeEach
    void setUp() {
        processor = new PatchItemProcessor(patchService);
    }

    @Test
    void process() throws IOException, URISyntaxException {
        when(patchService.getPatch(any())).thenReturn(patchMock);
        PatchMetadata patchMetadata = mock(PatchMetadata.class);

        Patch patch = processor.process(patchMetadata);

        assertThat(patch).isEqualTo(patchMock);
        verify(patchService).getPatch(any());
    }
}