package com.rabbitaats.alpha_experience.domain;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Handles events related to attaching, copying, and preserving Alpha Experience capability data.
 */
public class AlphaExperienceCapabilityEvents {

    /**
     * Attaches Alpha Experience data to player entities.
     * <p>
     * This event is called when capabilities are attached to an entity.
     * We check whether the entity is a Player, and if so, attach the AlphaExperienceProvider.
     */
    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(AlphaExperienceProvider.ID, new AlphaExperienceProvider());
        }
    }

    /**
     * Copies Alpha Experience data when the player entity is cloned.
     * <p>
     * Minecraft creates a new Player entity in some cases, such as death.
     * If we do not copy the capability data here, the player's Alpha Experience and Alpha Level would be reset.
     * <p>
     * reviveCaps() is required because the original player's capabilities may already be invalidated when this event is fired.
     * <p>
     * invalidateCaps() is called afterward to return the original player capability state to normal.
     */
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps();

        event.getOriginal().getCapability(AlphaExperienceProvider.ALPHA_EXPERIENCE).ifPresent(oldData ->
            event.getEntity().getCapability(AlphaExperienceProvider.ALPHA_EXPERIENCE).ifPresent(newData ->
                newData.copyFrom(oldData)
            )
        );

        event.getOriginal().invalidateCaps();
    }
}