package com.krazytop.leagueoflegends.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleEnum {
    TOP("top", "TOP"),
    JUNGLE("jungle", "JUNGLE"),
    MIDDLE("middle", "MIDDLE"),
    BOTTOM("bottom", "BOTTOM"),
    SUPPORT("support", "UTILITIES"),
    ALL_ROLES("all-roles", "ALL_ROLES");

    private final String name;
    private final String riotName;

    public static RoleEnum fromName(String name) {
        for (RoleEnum role : RoleEnum.values()) {
            if (role.getName().equals(name)) {
                return role;
            }
        }
        return ALL_ROLES;
    }

}
