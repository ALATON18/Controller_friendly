package com.alaton18.controllerfriendly.client.input;

import com.alaton18.controllerfriendly.ControllerFriendlyConfig;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;

public final class ControllerState {
	private final boolean[] buttons = new boolean[GLFW.GLFW_GAMEPAD_BUTTON_LAST + 1];
	private float leftX;
	private float leftY;
	private float rightX;
	private float rightY;
	private float leftTrigger;
	private float rightTrigger;

	public void read(GLFWGamepadState state) {
		for (int button = 0; button <= GLFW.GLFW_GAMEPAD_BUTTON_LAST; button++) {
			buttons[button] = state.buttons(button) == GLFW.GLFW_PRESS;
		}

		leftX = deadzone(state.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X), ControllerFriendlyConfig.LEFT_STICK_DEADZONE.get());
		leftY = deadzone(state.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y), ControllerFriendlyConfig.LEFT_STICK_DEADZONE.get());
		rightX = deadzone(state.axes(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X), ControllerFriendlyConfig.RIGHT_STICK_DEADZONE.get());
		rightY = deadzone(state.axes(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y), ControllerFriendlyConfig.RIGHT_STICK_DEADZONE.get());
		leftTrigger = normalizeTrigger(state.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER));
		rightTrigger = normalizeTrigger(state.axes(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER));
	}

	public boolean down(ControllerButton button) {
		if (button.isUnbound()) {
			return false;
		}
		if (button == ControllerButton.LEFT_TRIGGER || button == ControllerButton.LEFT_TRIGGER_HOLD) {
			return leftTrigger > ControllerFriendlyConfig.TRIGGER_DEADZONE.get();
		}
		if (button == ControllerButton.RIGHT_TRIGGER) {
			return rightTrigger > ControllerFriendlyConfig.TRIGGER_DEADZONE.get();
		}
		int glfwButton = button.glfwButton();
		return glfwButton >= 0 && glfwButton < buttons.length && buttons[glfwButton];
	}

	public float leftX() {
		return leftX;
	}

	public float leftY() {
		return leftY;
	}

	public float rightX() {
		return rightX;
	}

	public float rightY() {
		return rightY;
	}

	private static float deadzone(float value, double deadzone) {
		return Math.abs(value) < deadzone ? 0.0F : value;
	}

	private static float normalizeTrigger(float value) {
		return (value + 1.0F) / 2.0F;
	}
}
