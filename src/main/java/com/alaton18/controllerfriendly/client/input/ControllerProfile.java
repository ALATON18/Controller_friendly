package com.alaton18.controllerfriendly.client.input;

import com.alaton18.controllerfriendly.ControllerFriendly;
import com.alaton18.controllerfriendly.ControllerFriendlyConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.EnumMap;
import java.util.Map;

public final class ControllerProfile {
	private final Map<ControllerAction, ControllerBinding> bindings;

	private ControllerProfile(Map<ControllerAction, ControllerBinding> bindings) {
		this.bindings = bindings;
	}

	public ControllerButton buttonFor(ControllerAction action) {
		ControllerBinding binding = bindings.get(action);
		return binding == null ? ControllerButton.UNBOUND : binding.button();
	}

	public static ControllerProfile fromConfig() {
		EnumMap<ControllerAction, ControllerBinding> map = new EnumMap<>(ControllerAction.class);

		put(map, ControllerAction.USE_PLACE_ITEM, ControllerFriendlyConfig.USE_PLACE_ITEM);
		put(map, ControllerAction.ATTACK, ControllerFriendlyConfig.ATTACK);
		put(map, ControllerAction.HOTBAR_PREVIOUS, ControllerFriendlyConfig.HOTBAR_PREVIOUS);
		put(map, ControllerAction.HOTBAR_NEXT, ControllerFriendlyConfig.HOTBAR_NEXT);
		put(map, ControllerAction.RUN, ControllerFriendlyConfig.RUN);
		put(map, ControllerAction.SNEAK, ControllerFriendlyConfig.SNEAK);
		put(map, ControllerAction.INVENTORY, ControllerFriendlyConfig.INVENTORY);
		put(map, ControllerAction.CANCEL, ControllerFriendlyConfig.CANCEL);
		put(map, ControllerAction.JUMP_SELECT, ControllerFriendlyConfig.JUMP_SELECT);
		put(map, ControllerAction.CRAFT, ControllerFriendlyConfig.CRAFT);
		put(map, ControllerAction.MENU, ControllerFriendlyConfig.MENU);
		put(map, ControllerAction.MAP, ControllerFriendlyConfig.MAP);
		put(map, ControllerAction.PLAYER_LIST, ControllerFriendlyConfig.PLAYER_LIST);
		put(map, ControllerAction.DROP_ITEM, ControllerFriendlyConfig.DROP_ITEM);
		put(map, ControllerAction.RADIAL_MENU, ControllerFriendlyConfig.RADIAL_MENU);
		put(map, ControllerAction.CHAT, ControllerFriendlyConfig.CHAT);
		put(map, ControllerAction.QUEST_BOOK, ControllerFriendlyConfig.QUEST_BOOK);
		put(map, ControllerAction.INVENTORY_PICKUP_PLACE, ControllerFriendlyConfig.INVENTORY_PICKUP_PLACE);
		put(map, ControllerAction.INVENTORY_QUICK_MOVE, ControllerFriendlyConfig.INVENTORY_QUICK_MOVE);
		put(map, ControllerAction.INVENTORY_SPLIT_STACK, ControllerFriendlyConfig.INVENTORY_SPLIT_STACK);
		put(map, ControllerAction.INVENTORY_DROP, ControllerFriendlyConfig.INVENTORY_DROP);
		put(map, ControllerAction.INVENTORY_FAVOURITE_LOCK, ControllerFriendlyConfig.INVENTORY_FAVOURITE_LOCK);
		put(map, ControllerAction.TOGGLE_CURSOR, ControllerFriendlyConfig.TOGGLE_CURSOR);
		put(map, ControllerAction.PAGE_PREVIOUS, ControllerFriendlyConfig.PAGE_PREVIOUS);
		put(map, ControllerAction.PAGE_NEXT, ControllerFriendlyConfig.PAGE_NEXT);
		put(map, ControllerAction.KEYBOARD_SYMBOLS, ControllerFriendlyConfig.KEYBOARD_SYMBOLS);
		put(map, ControllerAction.KEYBOARD_SHIFT, ControllerFriendlyConfig.KEYBOARD_SHIFT);
		put(map, ControllerAction.KEYBOARD_COPY, ControllerFriendlyConfig.KEYBOARD_COPY);
		put(map, ControllerAction.KEYBOARD_PASTE, ControllerFriendlyConfig.KEYBOARD_PASTE);
		put(map, ControllerAction.KEYBOARD_CLEAR, ControllerFriendlyConfig.KEYBOARD_CLEAR);

		return new ControllerProfile(map);
	}

	private static void put(EnumMap<ControllerAction, ControllerBinding> map, ControllerAction action, ModConfigSpec.ConfigValue<String> value) {
		ControllerButton button = ControllerButton.byId(value.get()).orElseGet(() -> {
			ControllerFriendly.LOGGER.warn("Unknown controller binding '{}' for {}. Treating as unbound.", value.get(), action);
			return ControllerButton.UNBOUND;
		});
		map.put(action, new ControllerBinding(action, button));
	}
}
