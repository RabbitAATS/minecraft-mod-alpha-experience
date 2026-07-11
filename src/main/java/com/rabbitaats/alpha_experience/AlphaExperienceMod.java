package com.rabbitaats.alpha_experience;

import com.mojang.logging.LogUtils;
import com.rabbitaats.alpha_experience.command.AlphaExperienceCommand;
import com.rabbitaats.alpha_experience.domain.AlphaExperienceCapabilityEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

/**
 * Main mod class for Alpha Experience.
 *
 * This class is loaded by Forge when the mod starts.
 * It registers event handlers and commands used by the mod.
 */
@Mod(AlphaExperienceMod.MOD_ID)
public class AlphaExperienceMod {

    public static final String MOD_ID = "alpha_experience";

    public static final Logger LOGGER = LogUtils.getLogger();

    public AlphaExperienceMod() {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(AlphaExperienceCapabilityEvents.class);
    }

    /**
     * Registers custom commands when Minecraft builds the command dispatcher.
     */
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        AlphaExperienceCommand.register(event.getDispatcher());
    }
}