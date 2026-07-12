package com.rabbitaats.alpha_experience.domain;

import net.minecraft.nbt.CompoundTag;

import java.util.EnumMap;
import java.util.Map;

/**
 * Stores Alpha Experience progress and Alpha Level values for a single player.
 * <p>
 * This data is separated from Minecraft's vanilla experience system.
 * Each player has independent Alpha Experience progress and Alpha Level values
 * for each AlphaExperienceColor.
 */
public class AlphaExperiencePlayerData {

    private static final int MIN_ALPHA_EXPERIENCE = 0;
    private static final int MIN_ALPHA_LEVEL = 1;

    /**
     * Stores current Alpha Experience progress toward the next Alpha Level.
     * <p>
     * This is not total Alpha Experience.
     * <p>
     * Example:
     * AlphaLevel: 2
     * Progress:   75 / 200
     */
    private final Map<AlphaExperienceColor, Integer> alphaExperienceProgressMap =
        new EnumMap<>(AlphaExperienceColor.class);

    /**
     * Stores current Alpha Level for each color.
     * <p>
     * Alpha Level starts from 1 and never goes below 1.
     */
    private final Map<AlphaExperienceColor, Integer> alphaLevelMap =
        new EnumMap<>(AlphaExperienceColor.class);

    public AlphaExperiencePlayerData() {
        for (AlphaExperienceColor color : AlphaExperienceColor.values()) {
            alphaExperienceProgressMap.put(color, MIN_ALPHA_EXPERIENCE);
            alphaLevelMap.put(color, MIN_ALPHA_LEVEL);
        }
    }

    /**
     * Gets total Alpha Experience for the specified color.
     * <p>
     * Total Alpha Experience is not stored directly.
     * It is calculated from current Alpha Level and current progress.
     */
    public int getAlphaExperience(AlphaExperienceColor color) {
        int totalAlphaExperience = 0;
        int currentAlphaLevel = getAlphaLevel(color);

        for (int level = MIN_ALPHA_LEVEL; level < currentAlphaLevel; level++) {
            totalAlphaExperience += getRequiredAlphaExperienceForNextLevel(level);
        }

        totalAlphaExperience += getAlphaExperienceProgress(color);

        return Math.max(MIN_ALPHA_EXPERIENCE, totalAlphaExperience);
    }

    /**
     * Gets current Alpha Experience progress toward the next Alpha Level.
     */
    public int getAlphaExperienceProgress(AlphaExperienceColor color) {
        return alphaExperienceProgressMap.getOrDefault(color, MIN_ALPHA_EXPERIENCE);
    }

    public int getAlphaLevel(AlphaExperienceColor color) {
        return alphaLevelMap.getOrDefault(color, MIN_ALPHA_LEVEL);
    }

    /**
     * Adds Alpha Experience to the specified color.
     * <p>
     * Negative values are allowed for commands/debugging.
     * However, total Alpha Experience will never go below 0.
     * <p>
     * This method converts the current state into a temporary total value,
     * applies the amount, clamps it, and then recalculates level and progress.
     */
    public void addAlphaExperience(AlphaExperienceColor color, int amount) {
        int currentTotalAlphaExperience = getAlphaExperience(color);
        int newTotalAlphaExperience = Math.max(
            MIN_ALPHA_EXPERIENCE,
            currentTotalAlphaExperience + amount
        );

        recalculateLevelAndProgressFromTotalExperience(color, newTotalAlphaExperience);
    }

    /**
     * Adds Alpha Level to the specified color.
     * <p>
     * Negative values are allowed for commands/debugging.
     * However, Alpha Level will never go below 1.
     */
    public void addAlphaLevel(AlphaExperienceColor color, int amount) {
        int currentAlphaLevel = getAlphaLevel(color);
        int newAlphaLevel = Math.max(MIN_ALPHA_LEVEL, currentAlphaLevel + amount);

        alphaLevelMap.put(color, newAlphaLevel);

        clampProgressForCurrentLevel(color);
    }

    /**
     * Recalculates Alpha Level and progress from a temporary total Alpha Experience value.
     * <p>
     * The total value is not stored.
     * Only the resulting Alpha Level and current progress are stored.
     */
    private void recalculateLevelAndProgressFromTotalExperience(
        AlphaExperienceColor color,
        int totalAlphaExperience
    ) {
        int remainingAlphaExperience = Math.max(MIN_ALPHA_EXPERIENCE, totalAlphaExperience);
        int calculatedAlphaLevel = MIN_ALPHA_LEVEL;

        while (remainingAlphaExperience >= getRequiredAlphaExperienceForNextLevel(calculatedAlphaLevel)) {
            int requiredAlphaExperience = getRequiredAlphaExperienceForNextLevel(calculatedAlphaLevel);

            remainingAlphaExperience -= requiredAlphaExperience;
            calculatedAlphaLevel++;
        }

        alphaLevelMap.put(color, calculatedAlphaLevel);
        alphaExperienceProgressMap.put(color, remainingAlphaExperience);
    }

    /**
     * Clamps progress so it never becomes invalid for the current level.
     * <p>
     * This is mainly needed when Alpha Level is changed directly by command.
     */
    private void clampProgressForCurrentLevel(AlphaExperienceColor color) {
        int currentProgress = getAlphaExperienceProgress(color);
        int currentLevel = getAlphaLevel(color);
        int requiredExperience = getRequiredAlphaExperienceForNextLevel(currentLevel);

        int clampedProgress = Math.max(
            MIN_ALPHA_EXPERIENCE,
            Math.min(currentProgress, requiredExperience - 1)
        );

        alphaExperienceProgressMap.put(color, clampedProgress);
    }

    /**
     * Calculates the required Alpha Experience for the next level.
     * <p>
     * Current formula:
     * required exp = 100 + level^2 * 25
     */
    public int getRequiredAlphaExperienceForNextLevel(int alphaLevel) {
        int clampedAlphaLevel = Math.max(MIN_ALPHA_LEVEL, alphaLevel);

        return 100 + clampedAlphaLevel * clampedAlphaLevel * 25;
    }

    public void copyFrom(AlphaExperiencePlayerData source) {
        for (AlphaExperienceColor color : AlphaExperienceColor.values()) {
            this.alphaExperienceProgressMap.put(
                color,
                source.getAlphaExperienceProgress(color)
            );

            this.alphaLevelMap.put(
                color,
                source.getAlphaLevel(color)
            );
        }
    }

    public CompoundTag saveNBTData() {
        CompoundTag tag = new CompoundTag();

        for (AlphaExperienceColor color : AlphaExperienceColor.values()) {
            String colorName = color.getCommandName();

            tag.putInt(colorName + "AlphaExperienceProgress", getAlphaExperienceProgress(color));
            tag.putInt(colorName + "AlphaLevel", getAlphaLevel(color));
        }

        return tag;
    }

    public void loadNBTData(CompoundTag tag) {
        for (AlphaExperienceColor color : AlphaExperienceColor.values()) {
            String colorName = color.getCommandName();

            int alphaLevel = tag.contains(colorName + "AlphaLevel")
                ? tag.getInt(colorName + "AlphaLevel")
                : MIN_ALPHA_LEVEL;

            int alphaExperienceProgress;

            if (tag.contains(colorName + "AlphaExperienceProgress")) {
                alphaExperienceProgress = tag.getInt(colorName + "AlphaExperienceProgress");
            } else if (tag.contains(colorName + "AlphaExperience")) {
                /*
                 * Compatibility with older save data.
                 *
                 * Older versions used the key "AlphaExperience".
                 * At that time, it represented current progress, not total experience.
                 */
                alphaExperienceProgress = tag.getInt(colorName + "AlphaExperience");
            } else {
                alphaExperienceProgress = MIN_ALPHA_EXPERIENCE;
            }

            alphaLevelMap.put(color, Math.max(MIN_ALPHA_LEVEL, alphaLevel));
            alphaExperienceProgressMap.put(
                color,
                Math.max(MIN_ALPHA_EXPERIENCE, alphaExperienceProgress)
            );

            clampProgressForCurrentLevel(color);
        }
    }
}