package com.rabbitaats.alpha_experience.domain;

import com.rabbitaats.alpha_experience.AlphaExperienceMod;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Capability provider for Alpha Experience player data.
 * <p>
 * This class attaches AlphaExperiencePlayerData to a player entity.
 * The data can then be saved, loaded, and accessed through Forge's Capability system.
 */
@AutoRegisterCapability
public class AlphaExperienceProvider implements ICapabilitySerializable<CompoundTag> {

    public static final Capability<AlphaExperiencePlayerData> ALPHA_EXPERIENCE =
        CapabilityManager.get(new CapabilityToken<>() {
        });

    public static final ResourceLocation ID =
        new ResourceLocation(AlphaExperienceMod.MOD_ID, "alpha_experience");

    private final AlphaExperiencePlayerData data = new AlphaExperiencePlayerData();

    private final LazyOptional<AlphaExperiencePlayerData> optionalData =
        LazyOptional.of(() -> data);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(
        @NotNull Capability<T> capability,
        @Nullable Direction direction
    ) {
        if (capability == ALPHA_EXPERIENCE) {
            return optionalData.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return data.saveNBTData();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        data.loadNBTData(tag);
    }
}