package com.alaton18.controllerfriendly.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public final class VirtualCursor {
	private boolean enabled;
	private boolean positioned;
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

	public void enableForScreen(Minecraft minecraft) {
		enabled = true;
		centerIfNeeded(minecraft);
	}

	public void toggle(Minecraft minecraft) {
		enabled = !enabled;
		centerIfNeeded(minecraft);

		if (minecraft.player != null) {
			minecraft.player.displayClientMessage(Component.literal(enabled ? "Controller cursor enabled" : "Controller cursor disabled"), true);
		}
	}

	public void centerIfNeeded(Minecraft minecraft) {
		if (!positioned || isOutsideScreen(minecraft)) {
			x = minecraft.getWindow().getGuiScaledWidth() / 2.0D;
			y = minecraft.getWindow().getGuiScaledHeight() / 2.0D;
			positioned = true;
		}
	}

	public void move(Minecraft minecraft, double deltaX, double deltaY) {
		centerIfNeeded(minecraft);
		setPosition(minecraft, x + deltaX, y + deltaY);
	}

	public void setPosition(Minecraft minecraft, double x, double y) {
		double maxX = Math.max(0.0D, minecraft.getWindow().getGuiScaledWidth() - 1.0D);
		double maxY = Math.max(0.0D, minecraft.getWindow().getGuiScaledHeight() - 1.0D);
		this.x = clamp(x, 0.0D, maxX);
		this.y = clamp(y, 0.0D, maxY);
		this.positioned = true;
	}

	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
		this.positioned = true;
	}

	private boolean isOutsideScreen(Minecraft minecraft) {
		return x < 0.0D || y < 0.0D || x >= minecraft.getWindow().getGuiScaledWidth() || y >= minecraft.getWindow().getGuiScaledHeight();
	}

	private static double clamp(double value, double min, double max) {
		return Math.max(min, Math.min(max, value));
	}
}
