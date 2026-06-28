package com.alaton18.controllerfriendly.client.keyboard;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ControllerKeyboardScreen extends Screen {
	private final Screen parent;

	public ControllerKeyboardScreen(Screen parent) {
		super(Component.translatable("controller_friendly.keyboard.title"));
		this.parent = parent;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
		guiGraphics.drawCenteredString(this.font, "Keyboard UI placeholder", this.width / 2, this.height / 2, 0xAAAAAA);
	}

	@Override
	public void onClose() {
		this.minecraft.setScreen(parent);
	}
}
