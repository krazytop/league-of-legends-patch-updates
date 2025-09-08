package com.krazytop.leagueoflegends.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public enum QueueEnum {
    NORMAL("normal", List.of("14", "61", "400", "2", "430", "490")),
    SOLO_RANKED("solo-ranked", List.of("4", "420")),
    FLEX_RANKED("flex-ranked", List.of("6", "42", "440")),
    ARAM("aram", List.of("65", "100", "450")),
    URF("urf", List.of("76", "1900", "318", "900", "1010")),
    NEXUS_BLITZ("nexus-blitz", List.of("1200", "1300")),
    ONE_FOR_ALL("one-for-all", List.of("70", "1020")),
    ULTIMATE_SPELLBOOK("ultimate-spellbook", List.of("1400")),
    ARENA("arena", List.of("1700", "1710")),
    ALL_QUEUES("all-queues", List.of());

    private final String name;
    private final List<String> ids;

    public static QueueEnum fromName(String name) {
        for (QueueEnum queue : QueueEnum.values()) {
            if (queue.getName().equals(name)) {
                return queue;
            }
        }
        return ALL_QUEUES;
    }
}
