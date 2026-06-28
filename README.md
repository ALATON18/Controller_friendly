# Controller Friendly

Client-side NeoForge controller support for Minecraft 1.21.1.

Controller Friendly aims to make Java Minecraft feel closer to Bedrock/console controls while staying compatible with modded screens through slot snapping, a virtual cursor fallback, and a Minecraft-style on-screen keyboard.

## Target

- Minecraft: 1.21.1
- Loader: NeoForge
- Side: client-only
- Default prompt style: PlayStation-style
- Mod id: `controller_friendly`

## MVP focus

1. Gameplay controls
2. Inventory controls with slot snapping
3. Virtual cursor fallback for modded screens
4. Manual on-screen keyboard for text/search fields

## Current state

This repo has the first NeoForge/ModDevGradle scaffold, the design spec, config-backed default bindings, and a first-pass GLFW controller polling/input manager.

The code is intentionally early. Expect the first local import/build to need API fixes before real gameplay testing.

## Building

Use Java 21.

For now, either run with a locally installed Gradle:

```bash
gradle build
```

or regenerate the wrapper files locally:

```bash
gradle wrapper
./gradlew build
```

The wrapper properties are present, but the wrapper jar/scripts still need to be generated or copied in.

## Design

See [`SPEC.md`](SPEC.md) for the current design and [`TODO.md`](TODO.md) for the working task list.
