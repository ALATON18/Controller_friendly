package com.alaton18.controllerfriendly.client.input;

import org.lwjgl.glfw.GLFW;

import java.util.Locale;
import java.util.Optional;

public enum ControllerButton {
	UNBOUND("unbound", -1),
	BUTTON_SOUTH("button_south", GLFW.GLFW_GAMEPAD_BUTTON_A),
	BUTTON_EAST("button_east", GLFW.GLFW_GAMEPAD_BUTTON_B),
	BUTTON_WEST("button_west", GLFW.GLFW_GAMEPAD_BUTTON_X),
	BUTTON_NORTH("button_north", GLFW.GLFW_GAMEPAD_BUTTON_Y),
	LEFT_BUMPER("left_bumper", GLFW.GLFW_GAMEPAD_BUTTON_LEFT_BUMPER),
	RIGHT_BUMPER("right_bumper", GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER),
	BACK("back", GLFW.GLFW_GAMEPAD_BUTTON_BACK),
	START("start", GLFW.GLFW_GAMEPAD_BUTTON_START),
	GUIDE("guide", GLFW.GLFW_GAMEPAD_BUTTON_GUIDE),
	LEFT_STICK("left_stick", GLFW.GLFW_GAMEPAD_BUTTON_LEFT_THUMB),
	RIGHT_STICK("right_stick", GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_THUMB),
	DPAD_UP("dpad_up", GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP),
	DPAD_RIGHT("dpad_right", GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT),
	DPAD_DOWN("dpad_down", GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN),
	DPAD_LEFT("dpad_left", GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT),
	LEFT_TRIGGER("left_trigger", -1),
	RIGHT_TRIGGER("right_trigger", -1),
	TOUCHPAD("touchpad", GLFW.GLFW_GAMEPAD_BUTTON_BACK),
	TOUCHPAD_HOLD("touchpad_hold", GLFW.GLFW_GAMEPAD_BUTTON_BACK),
	LEFT_STICK_HOLD("left_stick_hold", GLFW.GLFW_GAMEPAD_BUTTON_LEFT_THUMB),
	LEFT_TRIGGER_HOLD("left_trigger_hold", -1);

	private final String id;
	private final int glfwButton;

	ControllerButton(String id, int glfwButton) {
		this.id = id;
		this.glfwButton = glfwButton;
	}

	public String id() {
		return id;
	}

	public int glfwButton() {
		return glfwButton;
	}

	public boolean isUnbound() {
		return this == UNBOUND;
	}

	public boolean isAxisButton() {
		return this == LEFT_TRIGGER || this == RIGHT_TRIGGER || this == LEFT_TRIGGER_HOLD;
	}

	public static Optional<ControllerButton> byId(String rawId) {
		if (rawId == null || rawId.isBlank()) {
			return Optional.of(UNBOUND);
		}

		String normalized = rawId.trim().toLowerCase(Locale.ROOT);
		for (ControllerButton button : values()) {
			if (button.id.equals(normalized)) {
				return Optional.of(button);
			}
		}
		return Optional.empty();
	}
}
