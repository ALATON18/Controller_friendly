package com.alaton18.controllerfriendly.client.input;

import com.alaton18.controllerfriendly.ControllerFriendly;
import com.alaton18.controllerfriendly.ControllerFriendlyConfig;
import com.alaton18.controllerfriendly.client.gui.VirtualCursor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;

import java.util.EnumSet;

public final class ControllerInputManager {
	private final ControllerState state = new ControllerState();
	private final VirtualCursor virtualCursor = new VirtualCursor();
	private final EnumSet<ControllerAction> lastDownActions = EnumSet.noneOf(ControllerAction.class);

	private ControllerProfile profile;
	private int activeController = -1;
	private boolean wasConnected;
	private int cursorHoldTicks;

	public void tick(Minecraft minecraft) {
		if (!ControllerFriendlyConfig.ENABLED.get()) {
			releaseGameplayKeys(minecraft);
			return;
		}

		ensureProfileLoaded();

		int controller = findController();
		if (controller == -1) {
			onControllerMissing(minecraft);
			return;
		}

		activeController = controller;
		wasConnected = true;

		GLFWGamepadState gamepadState = GLFWGamepadState.create();
		if (!GLFW.glfwGetGamepadState(activeController, gamepadState)) {
			onControllerMissing(minecraft);
			return;
		}

		state.read(gamepadState);

		if (minecraft.screen == null) {
			handleGameplay(minecraft);
		} else {
			handleScreen(minecraft);
		}

		rememberActionStates();
	}

	public VirtualCursor virtualCursor() {
		return virtualCursor;
	}

	private void ensureProfileLoaded() {
		if (profile == null) {
			profile = ControllerProfile.fromConfig();
		}
	}

	private int findController() {
		if (activeController != -1 && GLFW.glfwJoystickIsGamepad(activeController)) {
			return activeController;
		}

		for (int joystick = GLFW.GLFW_JOYSTICK_1; joystick <= GLFW.GLFW_JOYSTICK_LAST; joystick++) {
			if (GLFW.glfwJoystickIsGamepad(joystick)) {
				ControllerFriendly.LOGGER.info("Using gamepad {} ({})", joystick, GLFW.glfwGetGamepadName(joystick));
				return joystick;
			}
		}
		return -1;
	}

	private void onControllerMissing(Minecraft minecraft) {
		if (wasConnected) {
			wasConnected = false;
			activeController = -1;
			releaseGameplayKeys(minecraft);
			if (minecraft.player != null) {
				minecraft.player.displayClientMessage(Component.translatable("controller_friendly.message.controller_disconnected"), false);
			}
		}
	}

	private void handleGameplay(Minecraft minecraft) {
		Options options = minecraft.options;

		options.keyUp.setDown(state.leftY() < -0.25F);
		options.keyDown.setDown(state.leftY() > 0.25F);
		options.keyLeft.setDown(state.leftX() < -0.25F);
		options.keyRight.setDown(state.leftX() > 0.25F);

		options.keyJump.setDown(down(ControllerAction.JUMP_SELECT));
		options.keyUse.setDown(down(ControllerAction.USE_PLACE_ITEM));
		options.keyAttack.setDown(down(ControllerAction.ATTACK));
		options.keySprint.setDown(down(ControllerAction.RUN));

		if (minecraft.player != null) {
			double sensitivity = ControllerFriendlyConfig.CAMERA_SENSITIVITY.get();
			double yaw = state.rightX() * 12.0D * sensitivity;
			double pitch = state.rightY() * 12.0D * sensitivity;
			if (yaw != 0.0D || pitch != 0.0D) {
				minecraft.player.turn(yaw, pitch);
			}
		}

		if (pressed(ControllerAction.INVENTORY)) {
			click(options.keyInventory);
		}
		if (pressed(ControllerAction.DROP_ITEM)) {
			click(options.keyDrop);
		}
		if (pressed(ControllerAction.CHAT)) {
			minecraft.setScreen(new ChatScreen(""));
		}
		if (pressed(ControllerAction.MENU) || pressed(ControllerAction.CANCEL)) {
			minecraft.setScreen(new PauseScreen(false));
		}

		// TODO: hotbar bumper cycling, map, quest book, radial menu, and craft action.
	}

	private void handleScreen(Minecraft minecraft) {
		releaseGameplayKeys(minecraft);
		handleVirtualCursorHold(minecraft);

		if (pressed(ControllerAction.CANCEL) && minecraft.screen != null) {
			minecraft.screen.onClose();
		}

		// TODO: slot snapping, Cross-as-left-click/select, text field detection, keyboard screen, JEI/EMI page binds.
	}

	private void handleVirtualCursorHold(Minecraft minecraft) {
		if (down(ControllerAction.TOGGLE_CURSOR)) {
			cursorHoldTicks++;
			if (cursorHoldTicks == ControllerFriendlyConfig.VIRTUAL_CURSOR_HOLD_TICKS.get()) {
				virtualCursor.toggle(minecraft);
			}
		} else {
			cursorHoldTicks = 0;
		}
	}

	private void releaseGameplayKeys(Minecraft minecraft) {
		Options options = minecraft.options;
		options.keyUp.setDown(false);
		options.keyDown.setDown(false);
		options.keyLeft.setDown(false);
		options.keyRight.setDown(false);
		options.keyJump.setDown(false);
		options.keyUse.setDown(false);
		options.keyAttack.setDown(false);
		options.keySprint.setDown(false);
	}

	private boolean down(ControllerAction action) {
		return profile != null && state.down(profile.buttonFor(action));
	}

	private boolean pressed(ControllerAction action) {
		return down(action) && !lastDownActions.contains(action);
	}

	private void rememberActionStates() {
		lastDownActions.clear();
		for (ControllerAction action : ControllerAction.values()) {
			if (down(action)) {
				lastDownActions.add(action);
			}
		}
	}

	private static void click(KeyMapping keyMapping) {
		KeyMapping.click(keyMapping.getKey());
	}
}
