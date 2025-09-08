package com.krazytop.leagueoflegends.batch;

import com.krazytop.leagueoflegends.entity.Metadata;
import com.krazytop.leagueoflegends.entity.Patch;
import com.krazytop.leagueoflegends.repository.MetadataRepository;
import com.krazytop.leagueoflegends.repository.PatchRepository;
import com.krazytop.leagueoflegends.service.PatchService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

import static com.krazytop.leagueoflegends.service.PatchService.getSeasonFromVersion;

@Slf4j
@AllArgsConstructor
@Component
public class PatchJobListener implements JobExecutionListener {

    private final MetadataRepository metadataRepository;
    private final PatchRepository patchRepository;
    private final PatchService patchService;

    @Override
    public void afterJob(JobExecution jobExecution) {
        ExecutionContext jobContext = jobExecution.getExecutionContext();
        List<PatchMetadata> newPatchesMetadata = (List<PatchMetadata>) jobContext.get("newPatchesMetadata");
        if (jobExecution.getStatus().equals(BatchStatus.COMPLETED) && !Objects.requireNonNull(newPatchesMetadata).isEmpty()) {
            Metadata metadata = metadataRepository.findAll().getFirst();
            try {
                String currentPatch = patchService.getCurrentPatchVersion();
                metadata.setCurrentPatch(currentPatch);
                metadata.setCurrentSeason(getSeasonFromVersion(currentPatch));
                log.info("Metadata updated with current patch {} and season {}", metadata.getCurrentPatch(), metadata.getCurrentSeason());
            } catch (IOException | URISyntaxException e) {
                log.warn("'Current versions on live' API is unavailable");
                Patch latestPatch = patchRepository.findLatestPatch();
                metadata.setCurrentPatch(latestPatch.getPatchId());
                metadata.setCurrentSeason(latestPatch.getSeason());
            }
            metadataRepository.save(metadata);
        }
    }
}