package com.krazytop.leagueoflegends.batch.step;

import com.krazytop.leagueoflegends.batch.model.PatchMetadata;
import com.krazytop.leagueoflegends.entity.Patch;
import com.krazytop.leagueoflegends.service.PatchService;
import lombok.AllArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;

@AllArgsConstructor
@Component
public class PatchItemProcessor  implements ItemProcessor<PatchMetadata, Patch> {

    private final PatchService patchService;

    @Override
    public Patch process(PatchMetadata patchMetadata) throws IOException, URISyntaxException {
        return patchService.getPatch(patchMetadata);
    }

}
