package com.rabbitaats.alpha_experience.domain;

import java.util.Locale;

public enum AlphaExperienceColor {
    RED,
    GREEN,
    YELLOW;

    public static AlphaExperienceColor fromCommandName(String name) {
        return switch (name.toLowerCase(Locale.ROOT)) {
            case "red" -> RED;
            case "green" -> GREEN;
            case "yellow" -> YELLOW;
            default -> throw new IllegalArgumentException("Unknown AlphaExpColor: " + name);
        };
    }

    public String getCommandName() {
        return name().toLowerCase(Locale.ROOT);
    }
}