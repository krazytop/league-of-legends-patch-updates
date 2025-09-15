package com.krazytop.leagueoflegends.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RuneNomenclature extends Nomenclature {

    private List<List<RunePerkNomenclature>> perks = new ArrayList<>();

    @JsonProperty("slots")
    private void unpackPerks(List<JsonNode> nodes) {
        nodes.forEach(node -> this.perks.add(new ObjectMapper().convertValue(node.get("runes"), new TypeReference<>() {})));
    }
}
