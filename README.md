# HugoSMP Client

A client-side Fabric mod for the HugoSMP community: quality-of-life HUD
modules, a draggable HUD editor, waypoint beacons, and a couple of
convenience automations - all client-side, none of it a gameplay advantage.

> [!WARNING]
> This is an unofficial, community-built project. **We are not affiliated
> with HugoSMP.net** or any server operator. Use at your own discretion and
> in line with the rules of whichever server you play on.

## What this is not

No killaura, no ESP/X-ray, no reach, no autoclickers. Nothing here reads
information the vanilla client wouldn't already show you, and nothing here
automates combat. If a server bans client-side HUD/QoL mods in general,
that ban applies to this one too - check the rules first.

## Features

Every module below is off by default and lives behind the in-game settings
carousel (bind **Open HugoSMP Settings**, default `Right Shift`). Click a
card to toggle it; click again to open its own settings.

| Module | What it does |
|---|---|
| Coordinates HUD | Position, facing, optional biome |
| Keystrokes HUD | WASD + mouse buttons, lights up as you press them |
| CPS Counter | Left-click rate, with an optional peak readout |
| FPS Counter | Current frame rate, turns red below a threshold you set |
| Ping HUD | Server latency, turns red above a threshold you set |
| Clock HUD | Real-world time, 12/24h |
| Session Timer | How long you've been playing this session |
| Low Health Alert | Flashing warning below a health threshold you set |
| Armor HUD | Equipped armor with a durability bar per piece |
| Item Counter | Live count of one chosen item (e.g. Totems) in your inventory |
| Item Scale | Enlarges a curated set of valuable items, in your inventory *and* on the ground |
| Death Coordinates | Chat message + waypoint marker where you died |
| Waypoints | Beacon-beam markers with floating labels; set one manually with a keybind |
| Chat Timestamps | Prefixes chat with the time |
| Toggle Sprint / Toggle Sneak | Small input QoL - hold-to-toggle instead of hold-to-keep |
| Fullbright | Removes darkness (caves, night) entirely |
| Auto Reconnect | Reconnects after a kick, crash, or lost connection - never on a manual disconnect |
| AFK Mode | Builds on Auto Reconnect: after reconnecting, waits for the resource pack to load and re-sends `/afk` - useful for AFK-lobby servers like HugoSMP or Donut SMP |

### HUD Editor

Open with `Right Ctrl` (or the link in the settings screen). Every enabled
module renders at its real position over the actual game - no dimming, no
guesswork. Drag a module to reposition it, scroll over it to resize it.

## Configuration

Settings live in `config/hugosmp-client.json` in your instance directory and
are edited entirely in-game - there's no reason to hand-edit the file.

## Building & running

```
./gradlew build       # produces the mod jar under build/libs
./gradlew runClient   # launches a dev client with the mod loaded
```

Requires Java 25. Targets Minecraft 26.1.2 on Fabric Loader, with Fabric API
and Fabric Language Kotlin as dependencies.

## License

All rights reserved - see [LICENSE](LICENSE).
