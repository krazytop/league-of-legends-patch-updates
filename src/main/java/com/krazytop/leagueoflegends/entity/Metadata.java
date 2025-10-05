package com.krazytop.leagueoflegends.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

import static com.krazytop.leagueoflegends.service.PatchService.isVersionAfterAnOther;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Metadata")
public class Metadata {

    @Id
    private Integer id = 1;
    private Integer currentSeason;
    private String currentPatch;
    private Set<String> allPatches = new HashSet<>();

    public void sortPatches() {
        List<String> sorted = new ArrayList<>(allPatches);
        sorted.sort((a, b) -> {
            if (isVersionAfterAnOther(b, a)) return 1;
            else if (isVersionAfterAnOther(a, b)) return -1;
            else return 0;
        });
        allPatches = new LinkedHashSet<>(sorted);
    }
}
