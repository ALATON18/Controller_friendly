package com.alaton18.controllerfriendly;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(ControllerFriendly.MOD_ID)
public final class ControllerFriendly {
	public static final String MOD_ID = "controller_friendly";
	public static final String MOD_NAME = "Controller Friendly";
	public static final Logger LOGGER = LogUtils.getLogger();

	public ControllerFriendly(IEventBus modEventBus, ModContainer modContainer) {
		modContainer.registerConfig(ModConfig.Type.CLIENT, ControllerFriendlyConfig.SPEC);
		LOGGER.info("{} loaded", MOD_NAME);
	}
}
