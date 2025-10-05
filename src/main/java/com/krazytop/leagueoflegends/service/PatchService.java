package com.krazytop.leagueoflegends.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.krazytop.leagueoflegends.batch.model.PatchMetadata;
import com.krazytop.leagueoflegends.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PatchService {

    public static final List<String> SUPPORTED_LANGUAGES = List.of("fr_FR", "en_GB");
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Set<String> getAllVersions() throws IOException, URISyntaxException {
        String uri = "https://ddragon.leagueoflegends.com/api/versions.json";
        Set<String> allPatchesVersion = MAPPER.convertValue(MAPPER.readTree(new URI(uri).toURL()), new TypeReference<>() {});
        return allPatchesVersion.stream()
                .filter(version -> !version.contains("lol"))
                .filter(version -> !version.startsWith("0."))
                .collect(Collectors.toSet());
    }

    public Patch getPatch(PatchMetadata patchMetadata) throws IOException, URISyntaxException {
        Patch patch = new Patch(patchMetadata);
        log.info("Update patch {} for language {}", patch.getPatchId(), patch.getLanguage());
        addPatchChampions(patch);
        addPatchSummonerSpells(patch);
        addPatchItems(patch);
        addPatchRunes(patch);
        addPatchAugments(patch);
        addPatchQueues(patch);
        return patch;
    }

    private void addPatchChampions(Patch patch) throws IOException, URISyntaxException {
        String url = String.format("https://ddragon.leagueoflegends.com/cdn/%s/data/%s/champion.json", patch.getFullPatchId(), patch.getLanguage());
        Map<String, ChampionNomenclature> nomenclaturesMap = MAPPER.convertValue(MAPPER.readTree(new URI(url).toURL()).get("data"), new TypeReference<>() {});
        patch.setChampions(nomenclaturesMap.values().stream().toList());
    }

    private void addPatchSummonerSpells(Patch patch) throws IOException, URISyntaxException {
        String url = String.format("https://ddragon.leagueoflegends.com/cdn/%s/data/%s/summoner.json", patch.getFullPatchId(), patch.getLanguage());
        Map<String, SummonerSpellNomenclature> nomenclaturesMap = MAPPER.convertValue(MAPPER.readTree(new URI(url).toURL()).get("data"), new TypeReference<>() {});
        patch.setSummonerSpells(nomenclaturesMap.values().stream().toList());
    }

    private void addPatchItems(Patch patch) throws IOException, URISyntaxException {
        String url = String.format("https://ddragon.leagueoflegends.com/cdn/%s/data/%s/item.json", patch.getFullPatchId(), patch.getLanguage());
        Map<String, ItemNomenclature> nomenclaturesMap = MAPPER.convertValue(MAPPER.readTree(new URI(url).toURL()).get("data"), new TypeReference<>() {});
        nomenclaturesMap.forEach((id, nomenclature) -> nomenclature.setId(id));
        patch.setItems(nomenclaturesMap.values().stream().toList());
    }

    private void addPatchRunes(Patch patch) throws IOException, URISyntaxException {
        if (isVersionAfterAnOther(patch.getPatchId(), "8.0"))  {
            String url = String.format("https://ddragon.leagueoflegends.com/cdn/%s/data/%s/runesReforged.json", patch.getFullPatchId(), patch.getLanguage());
            patch.setRunes(MAPPER.convertValue(MAPPER.readTree(new URI(url).toURL()), new TypeReference<>() {}));
        }
    }

    private void addPatchAugments(Patch patch) throws IOException, URISyntaxException {
        if (isVersionAfterAnOther(patch.getPatchId(), "13.13")) {
            String url = String.format("https://raw.communitydragon.org/%s/cdragon/arena/%s.json", patch.getPatchId(), patch.getLanguage().toLowerCase());
            patch.setAugments(MAPPER.convertValue(MAPPER.readTree(new URI(url).toURL()).get("augments"), new TypeReference<>() {}));
        }
    }

    public void addPatchQueues(Patch patch) throws IOException, URISyntaxException {
        String version = isVersionAfterAnOther(patch.getPatchId(), "13.13") ? patch.getPatchId() : "13.14";
        String queueUri = String.format("https://raw.communitydragon.org/%s/plugins/rcp-be-lol-game-data/global/%s/v1/queues.json", version, patch.getLanguage().toLowerCase());
        if (isVersionAfterAnOther(version, "14.12")) {
            patch.setQueues(MAPPER.convertValue(MAPPER.convertValue(MAPPER.readTree(new URI(queueUri).toURL()), new TypeReference<>() {}), new TypeReference<>() {}));
        } else {
            Map<String, QueueNomenclature> nomenclaturesMap = MAPPER.convertValue(MAPPER.readTree(new URI(queueUri).toURL()), new TypeReference<>() {});
            nomenclaturesMap.forEach((id, nomenclature) -> nomenclature.setId(id));
            patch.setQueues(nomenclaturesMap.values().stream().toList());
        }
    }

    public String getCurrentPatchVersion() throws IOException, URISyntaxException {
        String uri = "https://ddragon.leagueoflegends.com/realms/euw.json";
        return MAPPER.readTree(new URI(uri).toURL()).get("v").asText().replaceAll("^(\\d+\\.\\d+).*", "$1");
    }

    public static String getWithoutFixVersion(String version) {
        return version.substring(0, version.lastIndexOf('.'));
    }

    public static Integer getSeasonFromVersion(String version) {
        return Integer.valueOf(version.substring(0, version.indexOf('.')));
    }

    public static boolean isVersionAfterAnOther(String versionToCheck, String compareToVersion) {
        String[] v1 = versionToCheck.split("\\.");
        String[] v2 = compareToVersion.split("\\.");

        int majorDiff = Integer.parseInt(v1[0]) - Integer.parseInt(v2[0]);
        return majorDiff != 0 ? majorDiff > 0 : Integer.parseInt(v1[1]) > Integer.parseInt(v2[1]);
    }

}