package com.krazytop.leagueoflegends.batch;

import com.krazytop.leagueoflegends.entity.Metadata;
import com.krazytop.leagueoflegends.entity.Patch;
import com.krazytop.leagueoflegends.repository.MetadataRepository;
import com.krazytop.leagueoflegends.repository.PatchRepository;
import com.krazytop.leagueoflegends.service.PatchService;
import lombok.AllArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class PatchItemWriter implements ItemWriter<Patch> {

    private final PatchRepository patchRepository;
    private final MetadataRepository metadataRepository;

    @Override
    public void write(Chunk<? extends Patch> newPatches) {
        patchRepository.saveAll(newPatches);
        Metadata metadata = metadataRepository.findAll().getFirst();
        metadata.getAllPatches().addAll(newPatches.getItems().stream().map(Patch::getPatchId).collect(Collectors.toSet()));
        metadataRepository.save(metadata);
    }

}
