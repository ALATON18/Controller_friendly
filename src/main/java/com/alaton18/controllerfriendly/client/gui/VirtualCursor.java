package com.alaton18.controllerfriendly.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public final class VirtualCursor {
	private boolean enabled;
	private double x;
	private double y;

	public boolean enabled() {
		return enabled;
	}

	public double x() {
		return x;
	}

	public double y() {
		return y;
	}

	public void toggle(Minecraft minecraft) {
		enabled = !enabled;
		if (minecraft.screen != null) {
			x = minecraft.getWindow().getGuiScaledWidth() / 2.0D;
			y = minecraft.getWindow().getGuiScaledHeight() / 2.0D;
		}

		if (minecraft.player != null) {
			minecraft.player.displayClientMessage(Component.literal(enabled ? "Controller cursor enabled" : "Controller cursor disabled"), true);
		}
	}

	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}
}
