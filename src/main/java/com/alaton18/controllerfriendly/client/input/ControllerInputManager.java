package com.alaton18.controllerfriendly.client.input;

import com.alaton18.controllerfriendly.ControllerFriendly;
import com.alaton18.controllerfriendly.ControllerFriendlyConfig;
import com.alaton18.controllerfriendly.client.gui.VirtualCursor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;

import java.util.EnumSet;

public final class ControllerInputManager {
	private static final double DPAD_CURSOR_STEP = 18.0D;
	private static final double STICK_CURSOR_SPEED = 11.0D;

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

		if (pressed(ControllerAction.USE_PLACE_ITEM)) {
			click(options.keyUse);
		}
		if (pressed(ControllerAction.ATTACK)) {
			click(options.keyAttack);
		}

		if (minecraft.player != null) {
			double sensitivity = ControllerFriendlyConfig.CAMERA_SENSITIVITY.get();
			double yaw = responseCurve(state.rightX()) * 28.0D * sensitivity;
			double pitch = responseCurve(state.rightY()) * 28.0D * sensitivity;
			if (yaw != 0.0D || pitch != 0.0D) {
				minecraft.player.turn(yaw, pitch);
			}
		}

		if (pressed(ControllerAction.INVENTORY) && minecraft.player != null) {
			minecraft.setScreen(new InventoryScreen(minecraft.player));
		}
		if (pressed(ControllerAction.DROP_ITEM)) {
			click(options.keyDrop);
		}
		if (pressed(ControllerAction.CHAT)) {
			minecraft.setScreen(new ChatScreen(""));
		}
		if (pressed(ControllerAction.MENU) || pressed(ControllerAction.CANCEL)) {
			minecraft.setScreen(new PauseScreen(true));
		}

		// TODO: hotbar bumper cycling, map, quest book, radial menu, and craft action.
	}

	private void handleScreen(Minecraft minecraft) {
		releaseGameplayKeys(minecraft);
		virtualCursor.enableForScreen(minecraft);
		moveScreenCursor(minecraft);
		syncSystemCursor(minecraft);
		handleVirtualCursorHold(minecraft);

		if (pressed(ControllerAction.CANCEL) && minecraft.screen != null) {
			minecraft.screen.onClose();
			return;
		}

		if (pressed(ControllerAction.JUMP_SELECT) || pressed(ControllerAction.INVENTORY_PICKUP_PLACE)) {
			clickScreen(minecraft, GLFW.GLFW_MOUSE_BUTTON_LEFT);
		}
		if (pressed(ControllerAction.INVENTORY_SPLIT_STACK)) {
			clickScreen(minecraft, GLFW.GLFW_MOUSE_BUTTON_RIGHT);
		}

		// TODO: true slot snapping, text field detection, keyboard screen, JEI/EMI page binds.
	}

	private void moveScreenCursor(Minecraft minecraft) {
		double dx = responseCurve(state.leftX()) * STICK_CURSOR_SPEED * ControllerFriendlyConfig.CURSOR_SPEED.get();
		double dy = responseCurve(state.leftY()) * STICK_CURSOR_SPEED * ControllerFriendlyConfig.CURSOR_SPEED.get();

		if (pressed(ControllerAction.DROP_ITEM)) {
			dy -= DPAD_CURSOR_STEP;
		}
		if (pressed(ControllerAction.RADIAL_MENU)) {
			dy += DPAD_CURSOR_STEP;
		}
		if (pressed(ControllerAction.QUEST_BOOK)) {
			dx -= DPAD_CURSOR_STEP;
		}
		if (pressed(ControllerAction.CHAT)) {
			dx += DPAD_CURSOR_STEP;
		}

		if (dx != 0.0D || dy != 0.0D) {
			virtualCursor.move(minecraft, dx, dy);
		}
	}

	private void syncSystemCursor(Minecraft minecraft) {
		double rawX = virtualCursor.x() * minecraft.getWindow().getScreenWidth() / minecraft.getWindow().getGuiScaledWidth();
		double rawY = virtualCursor.y() * minecraft.getWindow().getScreenHeight() / minecraft.getWindow().getGuiScaledHeight();
		GLFW.glfwSetCursorPos(minecraft.getWindow().getWindow(), rawX, rawY);
	}

	private void clickScreen(Minecraft minecraft, int mouseButton) {
		Screen screen = minecraft.screen;
		if (screen == null) {
			return;
		}

		double mouseX = virtualCursor.x();
		double mouseY = virtualCursor.y();
		screen.mouseClicked(mouseX, mouseY, mouseButton);
		screen.mouseReleased(mouseX, mouseY, mouseButton);
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

	private static double responseCurve(float value) {
		return value * Math.abs(value);
	}
}
