package com.rabbitaats.alpha_experience.domain;

import net.minecraft.nbt.CompoundTag;

import java.util.EnumMap;
import java.util.Map;

/**
 * Stores Alpha Experience and Alpha Level values for a single player.
 *
 * This data is separated from Minecraft's vanilla experience system.
 * Each player has independent Alpha Experience and Alpha Level values for each AlphaExperienceColor.
 */
public class AlphaExperiencePlayerData {

  private final Map<AlphaExperienceColor, Integer> alphaExperienceMap = new EnumMap<>(AlphaExperienceColor.class);
  private final Map<AlphaExperienceColor, Integer> alphaLevelMap = new EnumMap<>(AlphaExperienceColor.class);

  public AlphaExperiencePlayerData() {
    for (AlphaExperienceColor color : AlphaExperienceColor.values()) {
      alphaExperienceMap.put(color, 0);
      alphaLevelMap.put(color, 1);
    }
  }

  public int getAlphaExperience(AlphaExperienceColor color) {
    return alphaExperienceMap.getOrDefault(color, 0);
  }

  public int getAlphaLevel(AlphaExperienceColor color) {
    return alphaLevelMap.getOrDefault(color, 1);
  }

  public void setAlphaExperience(AlphaExperienceColor color, int alphaExperience) {
    alphaExperienceMap.put(color, Math.max(0, alphaExperience));
    checkLevelUp(color);
  }

  public void setAlphaLevel(AlphaExperienceColor color, int alphaLevel) {
    alphaLevelMap.put(color, Math.max(1, alphaLevel));
  }

  public void addAlphaExperience(AlphaExperienceColor color, int amount) {
    int currentAlphaExperience = getAlphaExperience(color);
    int newAlphaExperience = Math.max(0, currentAlphaExperience + amount);

    alphaExperienceMap.put(color, newAlphaExperience);

    checkLevelUp(color);
  }

  public void addAlphaLevel(AlphaExperienceColor color, int amount) {
    int currentAlphaLevel = getAlphaLevel(color);
    int newAlphaLevel = Math.max(1, currentAlphaLevel + amount);

    alphaLevelMap.put(color, newAlphaLevel);
  }

  private void checkLevelUp(AlphaExperienceColor color) {
    int currentAlphaExperience = getAlphaExperience(color);
    int currentAlphaLevel = getAlphaLevel(color);

    while (currentAlphaExperience >= getRequiredAlphaExperienceForNextLevel(currentAlphaLevel)) {
      int requiredAlphaExperience = getRequiredAlphaExperienceForNextLevel(currentAlphaLevel);

      currentAlphaExperience -= requiredAlphaExperience;
      currentAlphaLevel++;
    }

    alphaExperienceMap.put(color, currentAlphaExperience);
    alphaLevelMap.put(color, currentAlphaLevel);
  }

  public int getRequiredAlphaExperienceForNextLevel(int alphaLevel) {
    return 100 + alphaLevel * alphaLevel * 25;
  }

  public void copyFrom(AlphaExperiencePlayerData source) {
    for (AlphaExperienceColor color : AlphaExperienceColor.values()) {
      this.alphaExperienceMap.put(color, source.getAlphaExperience(color));
      this.alphaLevelMap.put(color, source.getAlphaLevel(color));
    }
  }

  public CompoundTag saveNBTData() {
    CompoundTag tag = new CompoundTag();

    for (AlphaExperienceColor color : AlphaExperienceColor.values()) {
      String colorName = color.getCommandName();

      tag.putInt(colorName + "AlphaExperience", getAlphaExperience(color));
      tag.putInt(colorName + "AlphaLevel", getAlphaLevel(color));
    }

    return tag;
  }

  public void loadNBTData(CompoundTag tag) {
    for (AlphaExperienceColor color : AlphaExperienceColor.values()) {
      String colorName = color.getCommandName();

      int alphaExperience = tag.getInt(colorName + "AlphaExperience");
      int alphaLevel = tag.contains(colorName + "AlphaLevel")
        ? tag.getInt(colorName + "AlphaLevel")
        : 1;

      alphaExperienceMap.put(color, Math.max(0, alphaExperience));
      alphaLevelMap.put(color, Math.max(1, alphaLevel));
    }
  }
}