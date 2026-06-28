package com.alaton18.controllerfriendly.client.input;

public record ControllerBinding(ControllerAction action, ControllerButton button) {
	public boolean isBound() {
		return button != ControllerButton.UNBOUND;
	}
}
