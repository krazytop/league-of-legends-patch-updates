package com.krazytop.leagueoflegends.batch.step;

import com.krazytop.leagueoflegends.batch.model.PatchMetadata;
import com.krazytop.leagueoflegends.entity.Metadata;
import com.krazytop.leagueoflegends.repository.MetadataRepository;
import com.krazytop.leagueoflegends.service.PatchService;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static com.krazytop.leagueoflegends.service.PatchService.SUPPORTED_LANGUAGES;
import static com.krazytop.leagueoflegends.service.PatchService.getWithoutFixVersion;

@AllArgsConstructor
@Component
public class FilterPatchesVersionTasklet implements Tasklet {

    private final PatchService patchService;
    private final MetadataRepository metadataRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        Set<String> allPatchesVersion = patchService.getAllVersions();
        Set<String> savedPatchesVersion = metadataRepository.findMetadata().orElse(new Metadata()).getAllPatches();

        List<PatchMetadata> newPatchesMetadata = allPatchesVersion.stream()
                .filter(version -> !savedPatchesVersion.contains(getWithoutFixVersion(version)))
                .flatMap(version -> SUPPORTED_LANGUAGES.stream()
                        .map(language -> new PatchMetadata(version, language)))
                .toList();

        chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
                .put("newPatchesMetadata", newPatchesMetadata);

        return RepeatStatus.FINISHED;
    }
}
