package com.krazytop.leagueoflegends.batch;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PatchMetadata {

    private String version;
    private String language;
}
