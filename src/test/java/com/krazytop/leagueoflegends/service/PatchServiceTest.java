package com.krazytop.leagueoflegends.service;

import com.krazytop.leagueoflegends.batch.model.PatchMetadata;
import com.krazytop.leagueoflegends.entity.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatchServiceTest {

    @InjectMocks
    private PatchService patchService;

    @ParameterizedTest
    @MethodSource("getPatchProvider")
    void getPatch(String version, boolean runesAreExpected, boolean augmentsAreExpected, boolean oldQueuesExpected) throws IOException, URISyntaxException {
        PatchMetadata patchMetadata = new PatchMetadata(version, "fr_FR");

        AtomicInteger urlConstructorCount = new AtomicInteger();
        try (MockedConstruction<URI> uriMock = mockConstruction(URI.class, (urlConstructor, context) -> {
            String fileName = identifyFileName(urlConstructorCount, runesAreExpected, augmentsAreExpected, oldQueuesExpected);
            when(urlConstructor.toURL()).thenReturn(getJson(fileName));
        })) {
            Patch patch = patchService.getPatch(patchMetadata);

            assertThat(patch.getChampions()).hasSize(1).first().isEqualTo(getExpectedChampionNomenclature());
            assertThat(patch.getSummonerSpells()).hasSize(1).first().isEqualTo(getExpectedSummonerSpellNomenclature());
            assertThat(patch.getItems()).hasSize(1).first().isEqualTo(getExpectedItemNomenclature());
            if (runesAreExpected) {
                assertThat(patch.getRunes()).hasSize(1).first().isEqualTo(getExpectedRuneNomenclature());
            } else {
                assertThat(patch.getAugments()).isNull();
            }
            if (augmentsAreExpected) {
                assertThat(patch.getAugments()).hasSize(1).first().isEqualTo(getExpectedAugmentNomenclature());
            } else {
                assertThat(patch.getAugments()).isNull();
            }
            assertThat(patch.getQueues()).hasSize(1).first().isEqualTo(getExpectedQueueNomenclature());

            int expectedConstructedMock = 6;
            if (!augmentsAreExpected) expectedConstructedMock--;
            if (!runesAreExpected) expectedConstructedMock--;

            assertThat(uriMock.constructed()).hasSize(expectedConstructedMock);
        }
    }

    private static Stream<Arguments> getPatchProvider() {
        return Stream.of(
                Arguments.of("14.13.1", true, true, false),
                Arguments.of("11.17.1", true, false, true),
                Arguments.of("7.13.1", false, false, true)
        );
    }

    @Test
    void getWithoutFixVersion() {
        assertThat(PatchService.getWithoutFixVersion("14.13.4")).isEqualTo("14.13");
    }

    @Test
    void getSeasonFromVersion() {
        assertThat(PatchService.getSeasonFromVersion("14.13.4")).isEqualTo(14);
    }

    @ParameterizedTest
    @MethodSource("isVersionAfterAnOtherProvider")
    void isVersionAfterAnOther(String versionToCheck, String compareToVersion, boolean expected) {
        assertThat(patchService.isVersionAfterAnOther(versionToCheck, compareToVersion)).isEqualTo(expected);
    }

    private static Stream<Arguments> isVersionAfterAnOtherProvider() {
        return Stream.of(
                Arguments.of("14.13", "14.12", true),
                Arguments.of("14.11", "14.12", false),
                Arguments.of("13.13", "14.12", false),
                Arguments.of("15.13", "14.12", true)
        );
    }

    @Test
    void getAllVersions() throws IOException, URISyntaxException {
        try (MockedConstruction<URI> uriMock = mockConstruction(URI.class, (urlConstructor, context) -> when(urlConstructor.toURL()).thenReturn(getJson("all-versions")))) {
            assertThat(patchService.getAllVersions()).containsExactly("15.18.1");
            assertThat(uriMock.constructed()).hasSize(1);
        }
    }

    @Test
    void getCurrentPatchVersion() throws IOException, URISyntaxException {
        try (MockedConstruction<URI> uriMock = mockConstruction(URI.class, (urlConstructor, context) -> when(urlConstructor.toURL()).thenReturn(getJson("current-version")))) {
            assertThat(patchService.getCurrentPatchVersion()).isEqualTo("14.19");
            assertThat(uriMock.constructed()).hasSize(1);
        }
    }

    private URL getJson(String nomFichier) {
        try {
            return new File(String.format("%s/src/test/resources/data/%s.json", System.getProperty("user.dir"), nomFichier)).toURI().toURL();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    private ChampionNomenclature getExpectedChampionNomenclature() {
        return ChampionNomenclature.builder()
                .id("266")
                .name("Aatrox")
                .image("Aatrox.png")
                .description("Autrefois, Aatrox et ses frères étaient honorés pour avoir défendu Shurima contre le Néant. Mais ils finirent par devenir une menace plus grande encore pour Runeterra : la ruse et la sorcellerie furent employées pour les battre. Cependant, après des...")
                .title("Épée des Darkin")
                .stats(Map.of("hp", 650F, "hpperlevel", 114F, "attackspeed", 0.651F))
                .tags(List.of("Fighter"))
                .build();
    }

    private SummonerSpellNomenclature getExpectedSummonerSpellNomenclature() {
        return SummonerSpellNomenclature.builder()
                .id("4")
                .name("Saut éclair")
                .image("SummonerFlash.png")
                .description("Vous téléporte sur une courte distance vers l'emplacement de votre curseur. (desc)")
                .tooltip("Vous téléporte sur une courte distance vers l'emplacement de votre curseur. (tooltip)")
                .cooldownBurn(300F)
                .build();
    }

    private ItemNomenclature getExpectedItemNomenclature() {
        return ItemNomenclature.builder()
                .id("1001")
                .name("Bottes")
                .image("1001.png")
                .description("<mainText><stats><attention>+25</attention> vitesse de déplacement</stats><br><br></mainText>")
                .plainText("Augmente légèrement la vitesse de déplacement.")
                .baseGold(300)
                .totalGold(301)
                .tags(List.of("Boots"))
                .stats(Map.of("FlatMovementSpeedMod", 25F))
                .toItems(List.of("3158"))
                .fromItems(List.of("3005"))
                .build();
    }

    private RuneNomenclature getExpectedRuneNomenclature() {
        return RuneNomenclature.builder()
                .id("8200")
                .name("Sorcellerie")
                .image("perk-images/Styles/7202_Sorcery.png")
                .perks(List.of(List.of(RunePerkNomenclature.builder()
                        .id("8214")
                        .name("Invocation d'Aery")
                        .image("perk-images/Styles/Sorcery/SummonAery/SummonAery.png")
                        .description("Vos attaques et vos compétences envoient Aery vers une cible, blessant les ennemis ou protégeant les alliés.")
                        .longDescription("Blesser un champion ennemi avec une attaque de base ou une compétence envoie Aery vers lui, lui infligeant 10 - 50 selon le niveau (+<scaleAP>0.05 puissance</scaleAP>).")
                        .build())))
                .build();
    }

    private AugmentNomenclature getExpectedAugmentNomenclature() {
        return AugmentNomenclature.builder()
                .id("108")
                .name("Autodestruction")
                .image("assets/ux/cherry/augments/icons/selfdestruct_large.png")
                .description("Vous commencez chaque manche avec une bombe attachée à vous. Au bout de @BombDelay@ sec, elle explose en infligeant des <trueDamage>dégâts bruts équivalents à @MaxHealthDamage*100@% des PV max</trueDamage> et <status>projette dans les airs</status> pendant @KnockupDuration@ sec.<br>")
                .dataValues(Map.of("BombDelay", 15F, "KnockupDuration", 0.75F, "MaxHealthDamage", 0.20000000298023224F, "Radius", 350F))
                .build();
    }

    private QueueNomenclature getExpectedQueueNomenclature() {
        return QueueNomenclature.builder()
                .id("420")
                .name("Classé en solo/duo")
                .description("Classé en solo/duo desc")
                .build();
    }

    private String identifyFileName(AtomicInteger urlConstructorCount, boolean runesAreExpected, boolean augmentsAreExpected, boolean oldQueuesExpected) {
        String queuesFileName = oldQueuesExpected ? "old-queues" : "queues";
        return switch (urlConstructorCount.getAndIncrement()) {
            case 0 -> "champions";
            case 1 -> "summoner-spells";
            case 2 -> "items";
            case 3 -> runesAreExpected ? "runes" : identifyFileName(urlConstructorCount, false, augmentsAreExpected, oldQueuesExpected);
            case 4 -> augmentsAreExpected ? "augments" : identifyFileName(urlConstructorCount, runesAreExpected, false, oldQueuesExpected);
            case 5 -> queuesFileName;
            default -> throw new IllegalArgumentException();
        };
    }
}
