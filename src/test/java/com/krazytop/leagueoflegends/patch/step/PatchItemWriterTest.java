package com.krazytop.leagueoflegends.patch.step;

import com.krazytop.leagueoflegends.batch.step.PatchItemWriter;
import com.krazytop.leagueoflegends.entity.Metadata;
import com.krazytop.leagueoflegends.entity.Patch;
import com.krazytop.leagueoflegends.repository.MetadataRepository;
import com.krazytop.leagueoflegends.repository.PatchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatchItemWriterTest {

    @InjectMocks
    private PatchItemWriter writer;

    @Mock
    private PatchRepository patchRepository;
    @Mock
    private MetadataRepository metadataRepository;

    @Test
    void write() {
        Patch patch1 = Patch.builder().patchId("14.1").build();
        Patch patch2 = Patch.builder().patchId("14.2").build();

        Metadata metadata = Metadata.builder().allPatches(new HashSet<>()).build();

        when(metadataRepository.findAll()).thenReturn(List.of(metadata));

        Chunk<Patch> chunk = new Chunk<>(List.of(patch1, patch2));

        writer.write(chunk);

        verify(patchRepository).saveAll(chunk);
        verify(metadataRepository).findAll();

        assertThat(metadata.getAllPatches()).containsExactlyInAnyOrder("14.1", "14.2");
        verify(metadataRepository).save(metadata);
    }

}
