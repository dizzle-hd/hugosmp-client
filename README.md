# HugoSMP Client

The official client-side companion mod for the HugoSMP community, built on Fabric.

This is **not** a hack client. It adds no gameplay-altering cheats (no killaura,
no ESP/xray, no reach, no fly). It focuses on clean quality-of-life and cosmetic
enhancements, in the same spirit as mods like Lunar Client or Badlion Client -
just the client-side extras, none of the unfair advantages.

## Features

- **Coordinates HUD** - a small, unobtrusive overlay showing your position,
  facing direction and (optionally) current biome. Neutral colors, no
  flashing effects, hidden automatically while the debug screen (F3) is open.
- More quality-of-life features are planned; the codebase is structured so
  new HUD elements and client-side features can be added without touching
  existing ones.

## Configuration

Settings are stored in `config/hugosmp-client.json` in your Minecraft instance
directory and can be edited in-game via the mod's settings screen (bind the
"Open HugoSMP Settings" key in Controls > Key Binds).

## Building & running

```
./gradlew build      # produces the mod jar under build/libs
./gradlew runClient   # launches a dev client with the mod loaded
```

Requires Java 25.

## License

All rights reserved - see [LICENSE](LICENSE).
