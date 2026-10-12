# PolyFullBright

PolyFullBright renders the world at a light level you choose, so caves and nights are not dark. It only changes what you see. Mob spawning and other game logic still use the real light levels.

## Features

- PolyFullBright is on after you install it. The "Enable FullBright" switch turns it off.
- The "Light Level" slider goes from 0 to 15 in whole steps and starts at 15.
- On 1.8.9, every block renders at the light level you pick. This also works with the Argentum renderer.
- On 1.21.1 and newer, the slider value replaces Minecraft's Brightness setting. Minecraft's own Brightness slider stops at 1, so any value above 1 is brighter than vanilla allows.
- The mod appears as FullBright in the OneConfig mod list.

## Supported versions

- Minecraft 1.8.9 on Ornithe.
- Minecraft 1.21.1, 1.21.4, 1.21.5, 1.21.8, 1.21.10, 1.21.11, 26.1, 26.2 and 26.3 on Fabric.

PolyFullBright needs [OneConfig](https://modrinth.com/mod/oneconfig) and Fabric Language Kotlin.

## Support

- Report bugs and ask questions on the [Polyfrost Discord](https://polyfrost.org/discord).
- Check [Polyfrost Status](https://status.polyfrost.org/) if a Polyfrost service is down.

## Building

```sh
./gradlew ":26.3:build"
```

Replace `26.3` with any supported Minecraft version. The build needs JDK 21 and JDK 25 installed.
