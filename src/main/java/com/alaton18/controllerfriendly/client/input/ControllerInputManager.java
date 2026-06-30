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
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;

import java.lang.reflect.Field;
import java.util.EnumSet;

public final class ControllerInputManager {
	private static final double DPAD_CURSOR_STEP = 18.0D;
	private static final double STICK_CURSOR_SPEED = 11.0D;
	private static final double CAMERA_SPEED = 1250.0D;

	private static Field leftPosField;
	private static Field topPosField;
	private static boolean lookedUpContainerPositionFields;

	private final ControllerState state = new ControllerState();
	private final VirtualCursor virtualCursor = new VirtualCursor();
	private final EnumSet<ControllerAction> lastDownActions = EnumSet.noneOf(ControllerAction.class);

	private ControllerProfile profile;
	private int activeController = -1;
	private boolean wasConnected;
	private int cursorHoldTicks;
	private long lastCameraNanos;

	public void tick(Minecraft minecraft) {
		if (!ControllerFriendlyConfig.ENABLED.get()) {
			releaseGameplayKeys(minecraft);
			return;
		}

		ensureProfileLoaded();

		if (!isWindowFocused(minecraft)) {
			releaseGameplayKeys(minecraft);
			lastCameraNanos = 0L;
			return;
		}

		if (!pollController()) {
			onControllerMissing(minecraft);
			return;
		}

		wasConnected = true;

		if (minecraft.screen == null) {
			handleGameplay(minecraft);
		} else {
			handleScreen(minecraft);
		}

		rememberActionStates();
	}

	public void renderFrame(Minecraft minecraft) {
		if (!ControllerFriendlyConfig.ENABLED.get() || !isWindowFocused(minecraft) || minecraft.screen != null || minecraft.player == null) {
			lastCameraNanos = 0L;
			return;
		}

		ensureProfileLoaded();

		if (!pollController()) {
			lastCameraNanos = 0L;
			return;
		}

		long now = System.nanoTime();
		if (lastCameraNanos == 0L) {
			lastCameraNanos = now;
			return;
		}

		double deltaSeconds = Math.min((now - lastCameraNanos) / 1_000_000_000.0D, 0.05D);
		lastCameraNanos = now;
		handleCamera(minecraft, deltaSeconds);
	}

	public VirtualCursor virtualCursor() {
		return virtualCursor;
	}

	private void ensureProfileLoaded() {
		if (profile == null) {
			profile = ControllerProfile.fromConfig();
		}
	}

	private boolean pollController() {
		int controller = findController();
		if (controller == -1) {
			return false;
		}

		activeController = controller;

		GLFWGamepadState gamepadState = GLFWGamepadState.create();
		if (!GLFW.glfwGetGamepadState(activeController, gamepadState)) {
			return false;
		}

		state.read(gamepadState);
		return true;
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

	private void handleCamera(Minecraft minecraft, double deltaSeconds) {
		double sensitivity = ControllerFriendlyConfig.CAMERA_SENSITIVITY.get();
		double yaw = responseCurve(state.rightX()) * CAMERA_SPEED * sensitivity * deltaSeconds;
		double pitch = responseCurve(state.rightY()) * CAMERA_SPEED * sensitivity * deltaSeconds;
		if (yaw != 0.0D || pitch != 0.0D) {
			minecraft.player.turn(yaw, pitch);
		}
	}

	private void handleScreen(Minecraft minecraft) {
		releaseGameplayKeys(minecraft);
		virtualCursor.enableForScreen(minecraft);
		boolean cursorMoved = moveScreenCursor(minecraft);
		if (cursorMoved) {
			syncSystemCursor(minecraft);
		}
		handleVirtualCursorHold(minecraft);

		if (pressed(ControllerAction.CANCEL) && minecraft.screen != null) {
			minecraft.screen.onClose();
			return;
		}

		if (pressed(ControllerAction.JUMP_SELECT)) {
			if (!quickMoveHoveredSlot(minecraft)) {
				clickScreen(minecraft, GLFW.GLFW_MOUSE_BUTTON_LEFT);
			}
		}
		if (pressed(ControllerAction.INVENTORY_PICKUP_PLACE)) {
			clickScreen(minecraft, GLFW.GLFW_MOUSE_BUTTON_LEFT);
		}
		if (pressed(ControllerAction.INVENTORY_SPLIT_STACK)) {
			clickScreen(minecraft, GLFW.GLFW_MOUSE_BUTTON_RIGHT);
		}

		// TODO: true slot snapping, text field detection, keyboard screen, JEI/EMI page binds.
	}

	private boolean moveScreenCursor(Minecraft minecraft) {
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
			return true;
		}
		return false;
	}

	private void syncSystemCursor(Minecraft minecraft) {
		if (!isWindowFocused(minecraft)) {
			return;
		}

		double rawX = virtualCursor.x() * minecraft.getWindow().getScreenWidth() / minecraft.getWindow().getGuiScaledWidth();
		double rawY = virtualCursor.y() * minecraft.getWindow().getScreenHeight() / minecraft.getWindow().getGuiScaledHeight();
		GLFW.glfwSetCursorPos(minecraft.getWindow().getWindow(), rawX, rawY);
	}

	private void clickScreen(Minecraft minecraft, int mouseButton) {
		Screen screen = minecraft.screen;
		if (screen == null) {
			return;
		}

		syncSystemCursor(minecraft);
		double mouseX = virtualCursor.x();
		double mouseY = virtualCursor.y();
		screen.mouseClicked(mouseX, mouseY, mouseButton);
		screen.mouseReleased(mouseX, mouseY, mouseButton);
	}

	private boolean quickMoveHoveredSlot(Minecraft minecraft) {
		Screen screen = minecraft.screen;
		if (!(screen instanceof AbstractContainerScreen<?> containerScreen) || minecraft.gameMode == null || minecraft.player == null) {
			return false;
		}

		Slot hoveredSlot = findSlotAtCursor(containerScreen);
		if (hoveredSlot == null || !hoveredSlot.hasItem()) {
			return false;
		}

		int menuSlotId = containerScreen.getMenu().slots.indexOf(hoveredSlot);
		if (menuSlotId < 0) {
			return false;
		}

		minecraft.gameMode.handleInventoryMouseClick(containerScreen.getMenu().containerId, menuSlotId, 0, ClickType.QUICK_MOVE, minecraft.player);
		return true;
	}

	private Slot findSlotAtCursor(AbstractContainerScreen<?> screen) {
		int left = getContainerPosition(screen, "leftPos");
		int top = getContainerPosition(screen, "topPos");
		double mouseX = virtualCursor.x();
		double mouseY = virtualCursor.y();

		for (Slot slot : screen.getMenu().slots) {
			double slotLeft = left + slot.x;
			double slotTop = top + slot.y;
			if (mouseX >= slotLeft - 1.0D && mouseX < slotLeft + 17.0D && mouseY >= slotTop - 1.0D && mouseY < slotTop + 17.0D) {
				return slot;
			}
		}
		return null;
	}

	private int getContainerPosition(AbstractContainerScreen<?> screen, String name) {
		try {
			Field field = containerPositionField(name);
			return field == null ? 0 : field.getInt(screen);
		} catch (IllegalAccessException exception) {
			ControllerFriendly.LOGGER.warn("Could not read container position {}", name, exception);
			return 0;
		}
	}

	private static Field containerPositionField(String name) {
		if (!lookedUpContainerPositionFields) {
			lookedUpContainerPositionFields = true;
			try {
				leftPosField = AbstractContainerScreen.class.getDeclaredField("leftPos");
				leftPosField.setAccessible(true);
				topPosField = AbstractContainerScreen.class.getDeclaredField("topPos");
				topPosField.setAccessible(true);
			} catch (NoSuchFieldException exception) {
				ControllerFriendly.LOGGER.warn("Could not find AbstractContainerScreen position fields", exception);
			}
		}

		return "leftPos".equals(name) ? leftPosField : topPosField;
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

	private static boolean isWindowFocused(Minecraft minecraft) {
		return GLFW.glfwGetWindowAttrib(minecraft.getWindow().getWindow(), GLFW.GLFW_FOCUSED) == GLFW.GLFW_TRUE;
	}

	private static double responseCurve(float value) {
		return value * Math.abs(value);
	}
}
