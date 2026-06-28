# Controller Friendly spec

## Project target

- **Minecraft:** 1.21.1
- **Loader:** NeoForge
- **Side:** client-only
- **Mod name:** Controller Friendly
- **Mod id:** `controller_friendly`
- **Default prompt style:** PlayStation-style
- **Config naming:** generic controller button names
- **First priority:** gameplay and inventory controls

## Core goals

Controller Friendly should make Java Minecraft feel closer to Bedrock/console controls while still working with modded Java UI.

The compatibility rule is:

1. Prefer smart screen navigation and inventory slot snapping.
2. Fall back to a virtual mouse cursor for unusual modded screens.
3. Keep the mod client-only and multiplayer-safe.
4. Avoid automation-like behaviour.

## Controller support

PlayStation layout is the priority, but the internal binding names should stay generic so Xbox, Switch, and other controllers can be supported later.

Every action must eventually be configurable through in-game settings. Until then, defaults live in the client config file.

Users must eventually be able to:

- rebind any action
- clear/unbind any action
- use button holds
- use button combos later if needed
- reset to defaults
- swap prompt/icon style

## Default gameplay controls

| Action | Default binding |
| --- | --- |
| Use/place item | L2 |
| Attack | R2 |
| Previous/next hotbar slot | L1/R1 |
| Move | Left stick |
| Run | Left stick press |
| Look around | Right stick |
| Inventory | Triangle |
| Back/Esc | Circle, always |
| Jump/select | Cross |
| Craft | Unbound / empty by default |
| Menu | Options |
| Map | Touchpad, if supported map mod is installed |
| Player list | Hold Touchpad |
| Drop item | D-pad up |
| Radial menu | D-pad down |
| Chat | D-pad right |
| Quest book | D-pad left, if supported quest mod is installed |

## Inventory controls

| Action | Default binding |
| --- | --- |
| Pick up/place stack | R2 |
| Quick move / shift-click | Triangle |
| Split stack / right-click | Square |
| Drop item | R3 |
| Favourite/lock item | Press L3, later feature |
| Toggle virtual cursor | Hold L3 for 3 seconds |
| JEI/EMI previous/next page | L1/R1 |

Inventory navigation should prioritize D-pad/stick slot snapping. Virtual cursor mode must exist from the start as a fallback.

## Text field behaviour

Cross should behave like mouse left-click/select:

- Cross on a text field focuses it and opens the controller keyboard.
- Cross on a normal button/slot activates/selects it.

Circle always backs out/closes where possible.

## On-screen keyboard controls

The keyboard has its own control scheme and should fit Minecraft's UI aesthetic.

| Action | Default binding |
| --- | --- |
| Symbols page | R3 |
| Shift/caps | Hold L2 |
| Clipboard copy | L1 |
| Clipboard paste | R1 |
| Clear field | Triangle |
| Back/close | Circle |

## Priority mod compatibility

First compatibility targets:

- JEI/EMI
- AE2
- Refined Storage
- Create
- Sophisticated Storage
- JourneyMap / FTB Maps
- FTB Quests

## MVP checklist

- [ ] NeoForge 1.21.1 project boots in client run config.
- [ ] Detect most recent connected controller.
- [ ] Use PlayStation-style prompts by default.
- [ ] Move/look/jump/use/attack/inventory/menu basics work.
- [ ] Inventory slot snapping works in vanilla containers.
- [ ] Virtual cursor fallback can be toggled with hold L3.
- [ ] Bottom-left cursor prompt appears on screens.
- [ ] Cross focuses text fields and opens controller keyboard.
- [ ] Controller disconnect falls back to KBM and shows client chat message.
