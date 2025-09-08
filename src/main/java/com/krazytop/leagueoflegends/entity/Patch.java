package com.krazytop.leagueoflegends.entity;

import com.krazytop.leagueoflegends.batch.PatchMetadata;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

import static com.krazytop.leagueoflegends.service.PatchService.getSeasonFromVersion;
import static com.krazytop.leagueoflegends.service.PatchService.getWithoutFixVersion;

@Data
@NoArgsConstructor
@Document(collection = "Patch")
public class Patch {

    public Patch(PatchMetadata patchMetadata) {
        this.fullPatchId = patchMetadata.getVersion();
        this.patchId = getWithoutFixVersion(fullPatchId);
        this.language = patchMetadata.getLanguage();
        this.id = patchId + "_" + language;
        this.season = getSeasonFromVersion(patchMetadata.getVersion());
    }

    private String id;
    private String patchId;
    private String language;
    private Integer season;
    @Transient private String fullPatchId;
    private List<ChampionNomenclature> champions;
    private List<ItemNomenclature> items;
    private List<SummonerSpellNomenclature> summonerSpells;
    private List<AugmentNomenclature> augments;
    private List<RuneNomenclature> runes;
    private List<QueueNomenclature> queues;

}
