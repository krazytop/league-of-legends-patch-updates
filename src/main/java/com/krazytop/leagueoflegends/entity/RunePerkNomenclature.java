package com.krazytop.leagueoflegends.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RunePerkNomenclature extends Nomenclature {

    @JsonAlias("longDesc")
    private String longDescription;

}