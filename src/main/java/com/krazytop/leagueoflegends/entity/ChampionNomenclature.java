package com.krazytop.leagueoflegends.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "ChampionNomenclature")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChampionNomenclature extends Nomenclature {

    private String title;
    private Map<String, Integer> stats;
    private List<String> tags;

    @JsonProperty("key")
    private void unpackId(JsonNode node) {
        this.setId(node.asText());
    }
}
