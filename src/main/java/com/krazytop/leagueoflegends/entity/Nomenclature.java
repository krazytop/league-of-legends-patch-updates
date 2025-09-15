package com.krazytop.leagueoflegends.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public abstract class Nomenclature {

    private String id;
    private String name;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String image;
    @JsonAlias({"blurb", "shortDesc", "desc"})
    private String description;

    @JsonProperty("image")
    private void unpackImage(JsonNode node) {
        this.setImage(node.get("full").asText());
    }

    @JsonProperty("icon")
    private void unpackIcon(JsonNode icon) {
        this.setImage(icon.asText());
    }
}
