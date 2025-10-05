package com.krazytop.leagueoflegends.batch.step;

import com.krazytop.leagueoflegends.entity.Metadata;
import com.krazytop.leagueoflegends.entity.Patch;
import com.krazytop.leagueoflegends.repository.MetadataRepository;
import com.krazytop.leagueoflegends.repository.PatchRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class PatchItemWriter implements ItemWriter<Patch> {

    private final PatchRepository patchRepository;
    private final MetadataRepository metadataRepository;

    @Override
    public void write(Chunk<? extends Patch> newPatches) {
        patchRepository.saveAll(newPatches);
        Metadata metadata = metadataRepository.findMetadata().orElse(new Metadata());
        metadata.getAllPatches().addAll(newPatches.getItems().stream().map(Patch::getPatchId).collect(Collectors.toSet()));
        metadata.sortPatches();
        metadataRepository.save(metadata);
    }

}
