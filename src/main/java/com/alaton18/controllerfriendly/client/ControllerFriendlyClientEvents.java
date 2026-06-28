package com.alaton18.controllerfriendly.client;

import com.alaton18.controllerfriendly.ControllerFriendly;
import com.alaton18.controllerfriendly.client.input.ControllerInputManager;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = ControllerFriendly.MOD_ID, value = Dist.CLIENT)
public final class ControllerFriendlyClientEvents {
	private static final ControllerInputManager INPUT = new ControllerInputManager();

	private ControllerFriendlyClientEvents() {
	}

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent.Post event) {
		INPUT.tick(Minecraft.getInstance());
	}
}
