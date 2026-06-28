package com.alaton18.controllerfriendly;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ControllerFriendlyConfig {
	private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

	public static final ModConfigSpec.BooleanValue ENABLED = BUILDER
			.comment("Enable Controller Friendly input handling.")
			.define("enabled", true);

	public static final ModConfigSpec.DoubleValue LEFT_STICK_DEADZONE = BUILDER
			.comment("Deadzone for movement stick axes.")
			.defineInRange("input.leftStickDeadzone", 0.18D, 0.0D, 0.95D);

	public static final ModConfigSpec.DoubleValue RIGHT_STICK_DEADZONE = BUILDER
			.comment("Deadzone for camera/cursor stick axes.")
			.defineInRange("input.rightStickDeadzone", 0.15D, 0.0D, 0.95D);

	public static final ModConfigSpec.DoubleValue TRIGGER_DEADZONE = BUILDER
			.comment("Deadzone for L2/R2 trigger axes.")
			.defineInRange("input.triggerDeadzone", 0.25D, 0.0D, 0.95D);

	public static final ModConfigSpec.DoubleValue CAMERA_SENSITIVITY = BUILDER
			.comment("Multiplier for right-stick camera movement.")
			.defineInRange("camera.sensitivity", 1.0D, 0.1D, 5.0D);

	public static final ModConfigSpec.DoubleValue CURSOR_SPEED = BUILDER
			.comment("Virtual cursor speed multiplier.")
			.defineInRange("cursor.speed", 1.0D, 0.1D, 5.0D);

	public static final ModConfigSpec.BooleanValue TOGGLE_SPRINT = BUILDER
			.comment("Use toggle sprint instead of hold sprint.")
			.define("gameplay.toggleSprint", false);

	public static final ModConfigSpec.BooleanValue TOGGLE_SNEAK = BUILDER
			.comment("Use toggle sneak instead of hold sneak.")
			.define("gameplay.toggleSneak", false);

	public static final ModConfigSpec.ConfigValue<String> PROMPT_STYLE = BUILDER
			.comment("Prompt icon style. Planned values: playstation, xbox, switch, generic.")
			.define("ui.promptStyle", "playstation");

	public static final ModConfigSpec.IntValue VIRTUAL_CURSOR_HOLD_TICKS = BUILDER
			.comment("Ticks L3 must be held to toggle virtual cursor. 60 ticks = 3 seconds.")
			.defineInRange("cursor.holdTicks", 60, 1, 200);

	public static final ModConfigSpec.ConfigValue<String> USE_PLACE_ITEM = binding("bindings.gameplay.usePlaceItem", "left_trigger");
	public static final ModConfigSpec.ConfigValue<String> ATTACK = binding("bindings.gameplay.attack", "right_trigger");
	public static final ModConfigSpec.ConfigValue<String> HOTBAR_PREVIOUS = binding("bindings.gameplay.hotbarPrevious", "left_bumper");
	public static final ModConfigSpec.ConfigValue<String> HOTBAR_NEXT = binding("bindings.gameplay.hotbarNext", "right_bumper");
	public static final ModConfigSpec.ConfigValue<String> RUN = binding("bindings.gameplay.run", "left_stick");
	public static final ModConfigSpec.ConfigValue<String> INVENTORY = binding("bindings.gameplay.inventory", "button_west");
	public static final ModConfigSpec.ConfigValue<String> CANCEL = binding("bindings.gameplay.cancel", "button_east");
	public static final ModConfigSpec.ConfigValue<String> JUMP_SELECT = binding("bindings.gameplay.jumpSelect", "button_south");
	public static final ModConfigSpec.ConfigValue<String> CRAFT = binding("bindings.gameplay.craft", "unbound");
	public static final ModConfigSpec.ConfigValue<String> MENU = binding("bindings.gameplay.menu", "start");
	public static final ModConfigSpec.ConfigValue<String> MAP = binding("bindings.gameplay.map", "touchpad");
	public static final ModConfigSpec.ConfigValue<String> PLAYER_LIST = binding("bindings.gameplay.playerList", "touchpad_hold");
	public static final ModConfigSpec.ConfigValue<String> DROP_ITEM = binding("bindings.gameplay.dropItem", "dpad_up");
	public static final ModConfigSpec.ConfigValue<String> RADIAL_MENU = binding("bindings.gameplay.radialMenu", "dpad_down");
	public static final ModConfigSpec.ConfigValue<String> CHAT = binding("bindings.gameplay.chat", "dpad_right");
	public static final ModConfigSpec.ConfigValue<String> QUEST_BOOK = binding("bindings.gameplay.questBook", "dpad_left");

	public static final ModConfigSpec.ConfigValue<String> INVENTORY_PICKUP_PLACE = binding("bindings.inventory.pickupPlace", "right_trigger");
	public static final ModConfigSpec.ConfigValue<String> INVENTORY_QUICK_MOVE = binding("bindings.inventory.quickMove", "button_west");
	public static final ModConfigSpec.ConfigValue<String> INVENTORY_SPLIT_STACK = binding("bindings.inventory.splitStack", "unbound");
	public static final ModConfigSpec.ConfigValue<String> INVENTORY_DROP = binding("bindings.inventory.drop", "right_stick");
	public static final ModConfigSpec.ConfigValue<String> INVENTORY_FAVOURITE_LOCK = binding("bindings.inventory.favouriteLock", "left_stick");
	public static final ModConfigSpec.ConfigValue<String> TOGGLE_CURSOR = binding("bindings.inventory.toggleCursor", "left_stick_hold");
	public static final ModConfigSpec.ConfigValue<String> PAGE_PREVIOUS = binding("bindings.inventory.pagePrevious", "left_bumper");
	public static final ModConfigSpec.ConfigValue<String> PAGE_NEXT = binding("bindings.inventory.pageNext", "right_bumper");

	public static final ModConfigSpec.ConfigValue<String> KEYBOARD_SYMBOLS = binding("bindings.keyboard.symbols", "right_stick");
	public static final ModConfigSpec.ConfigValue<String> KEYBOARD_SHIFT = binding("bindings.keyboard.shift", "left_trigger_hold");
	public static final ModConfigSpec.ConfigValue<String> KEYBOARD_COPY = binding("bindings.keyboard.copy", "left_bumper");
	public static final ModConfigSpec.ConfigValue<String> KEYBOARD_PASTE = binding("bindings.keyboard.paste", "right_bumper");
	public static final ModConfigSpec.ConfigValue<String> KEYBOARD_CLEAR = binding("bindings.keyboard.clear", "button_west");

	public static final ModConfigSpec SPEC = BUILDER.build();

	private ControllerFriendlyConfig() {
	}

	private static ModConfigSpec.ConfigValue<String> binding(String path, String defaultValue) {
		return BUILDER
				.comment("Controller binding. Use generic button names; use 'unbound' to disable.")
				.define(path, defaultValue);
	}
}
