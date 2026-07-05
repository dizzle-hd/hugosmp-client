package com.julian.client.gui

import com.julian.client.config.HugosmpConfig
import com.julian.client.config.ScalableItems
import net.minecraft.client.gui.screens.Screen

/**
 * One entry in the settings carousel. Kept intentionally minimal - the
 * carousel itself owns all rendering and interaction; adding a new setting
 * means adding one object here and one line in [SettingsCards.all]. Every
 * card's actual settings (enable toggle plus anything module-specific) live
 * behind [createSettingsScreen], opened by clicking the card - the card face
 * itself is just a preview, not directly interactive.
 */
sealed interface SettingsCard {
    val title: String
    val description: String

    fun isEnabled(config: HugosmpConfig): Boolean
    fun setEnabled(config: HugosmpConfig, enabled: Boolean)

    fun createSettingsScreen(parent: Screen): Screen
}

private fun enabledRow(card: SettingsCard): ToggleOption =
    ToggleOption("Enabled", card::isEnabled, card::setEnabled)

private val textColorPresets = listOf(0xE0E0E0, 0xFFFFFF, 0x7AA2FF, 0x7CFC98, 0xFFD479)
private fun textColorName(color: Int): String = when (color) {
    0xE0E0E0 -> "Light Gray"
    0xFFFFFF -> "White"
    0x7AA2FF -> "Blue"
    0x7CFC98 -> "Green"
    0xFFD479 -> "Amber"
    else -> "Custom"
}

private val opacityPresets = listOf(40, 90, 140, 200, 255)
private fun opacityName(alpha: Int): String = when (alpha) {
    40 -> "Faint"
    90 -> "Medium"
    140 -> "High"
    200 -> "Very High"
    255 -> "Solid"
    else -> "Custom"
}

object CoordinatesSettingsCard : SettingsCard {
    override val title: String = "Coordinates HUD"
    override val description: String = "Shows your position and facing direction"
    override fun isEnabled(config: HugosmpConfig) = config.coordsHudEnabled
    override fun setEnabled(config: HugosmpConfig, enabled: Boolean) {
        config.coordsHudEnabled = enabled
    }

    override fun createSettingsScreen(parent: Screen): Screen = ModuleOptionsScreen(
        parent, "Coordinates HUD Settings",
        listOf(
            enabledRow(this),
            ToggleOption("Show Biome", { it.coordsHudShowBiome }, { c, v -> c.coordsHudShowBiome = v }),
            CycleOption("Text Color", textColorPresets, ::textColorName, { it.coordsHudTextColor }, { c, v -> c.coordsHudTextColor = v }),
            CycleOption("Background Opacity", opacityPresets, ::opacityName, { it.coordsHudBackgroundAlpha }, { c, v -> c.coordsHudBackgroundAlpha = v })
        )
    )
}

object KeystrokesSettingsCard : SettingsCard {
    override val title: String = "Keystrokes HUD"
    override val description: String = "Shows WASD and mouse buttons as you press them"
    override fun isEnabled(config: HugosmpConfig) = config.keystrokesHudEnabled
    override fun setEnabled(config: HugosmpConfig, enabled: Boolean) {
        config.keystrokesHudEnabled = enabled
    }

    override fun createSettingsScreen(parent: Screen): Screen = ModuleOptionsScreen(
        parent, "Keystrokes HUD Settings",
        listOf(
            enabledRow(this),
            ToggleOption("Show Mouse Buttons", { it.keystrokesShowMouseButtons }, { c, v -> c.keystrokesShowMouseButtons = v })
        )
    )
}

object CpsSettingsCard : SettingsCard {
    override val title: String = "CPS Counter"
    override val description: String = "Shows your left-click clicks per second"
    override fun isEnabled(config: HugosmpConfig) = config.cpsHudEnabled
    override fun setEnabled(config: HugosmpConfig, enabled: Boolean) {
        config.cpsHudEnabled = enabled
    }

    override fun createSettingsScreen(parent: Screen): Screen = ModuleOptionsScreen(
        parent, "CPS Counter Settings",
        listOf(
            enabledRow(this),
            ToggleOption("Show Peak CPS", { it.cpsShowPeak }, { c, v -> c.cpsShowPeak = v })
        )
    )
}

object FpsSettingsCard : SettingsCard {
    override val title: String = "FPS Counter"
    override val description: String = "Shows your current frames per second"
    override fun isEnabled(config: HugosmpConfig) = config.fpsHudEnabled
    override fun setEnabled(config: HugosmpConfig, enabled: Boolean) {
        config.fpsHudEnabled = enabled
    }

    override fun createSettingsScreen(parent: Screen): Screen = ModuleOptionsScreen(
        parent, "FPS Counter Settings",
        listOf(
            enabledRow(this),
            SliderOption(
                "Warn Below", 10f, 120f, { it.fpsWarnThreshold }, { c, v -> c.fpsWarnThreshold = v },
                { "${it.toInt()} FPS" }
            )
        )
    )
}

object PingSettingsCard : SettingsCard {
    override val title: String = "Ping HUD"
    override val description: String = "Shows your connection latency to the server"
    override fun isEnabled(config: HugosmpConfig) = config.pingHudEnabled
    override fun setEnabled(config: HugosmpConfig, enabled: Boolean) {
        config.pingHudEnabled = enabled
    }

    override fun createSettingsScreen(parent: Screen): Screen = ModuleOptionsScreen(
        parent, "Ping HUD Settings",
        listOf(
            enabledRow(this),
            SliderOption(
                "Warn Above", 50f, 500f, { it.pingWarnThreshold }, { c, v -> c.pingWarnThreshold = v },
                { "${it.toInt()} ms" }
            )
        )
    )
}

object ClockSettingsCard : SettingsCard {
    override val title: String = "Clock HUD"
    override val description: String = "Shows the current real-world time"
    override fun isEnabled(config: HugosmpConfig) = config.clockHudEnabled
    override fun setEnabled(config: HugosmpConfig, enabled: Boolean) {
        config.clockHudEnabled = enabled
    }

    override fun createSettingsScreen(parent: Screen): Screen = ModuleOptionsScreen(
        parent, "Clock HUD Settings",
        listOf(
            enabledRow(this),
            ToggleOption("24-Hour Format", { it.clockUse24Hour }, { c, v -> c.clockUse24Hour = v })
        )
    )
}

object SessionTimerSettingsCard : SettingsCard {
    override val title: String = "Session Timer"
    override val description: String = "Shows how long you have been playing this session"
    override fun isEnabled(config: HugosmpConfig) = config.sessionTimerHudEnabled
    override fun setEnabled(config: HugosmpConfig, enabled: Boolean) {
        config.sessionTimerHudEnabled = enabled
    }

    override fun createSettingsScreen(parent: Screen): Screen = ModuleOptionsScreen(
        parent, "Session Timer Settings",
        listOf(
            enabledRow(this),
            ToggleOption("Show Seconds", { it.sessionTimerShowSeconds }, { c, v -> c.sessionTimerShowSeconds = v })
        )
    )
}

object LowHealthAlertSettingsCard : SettingsCard {
    override val title: String = "Low Health Alert"
    override val description: String = "Flashes a warning below the chosen health"
    override fun isEnabled(config: HugosmpConfig) = config.lowHealthAlertEnabled
    override fun setEnabled(config: HugosmpConfig, enabled: Boolean) {
        config.lowHealthAlertEnabled = enabled
    }

    override fun createSettingsScreen(parent: Screen): Screen = ModuleOptionsScreen(
        parent, "Low Health Alert Settings",
        listOf(
            enabledRow(this),
            SliderOption(
                "Trigger Below", 1f, 15f, { it.lowHealthThreshold }, { c, v -> c.lowHealthThreshold = v },
                { "${"%.1f".format(it / 2f)} hearts" }
            )
        )
    )
}

object DeathCoordsSettingsCard : SettingsCard {
    override val title: String = "Death Coordinates"
    override val description: String = "Sends your death location to chat"
    override fun isEnabled(config: HugosmpConfig) = config.deathCoordsEnabled
    override fun setEnabled(config: HugosmpConfig, enabled: Boolean) {
        config.deathCoordsEnabled = enabled
    }

    override fun createSettingsScreen(parent: Screen): Screen = ModuleOptionsScreen(
        parent, "Death Coordinates Settings",
        listOf(
            enabledRow(this),
            ToggleOption("Include Dimension", { it.deathCoordsIncludeDimension }, { c, v -> c.deathCoordsIncludeDimension = v })
        )
    )
}

object ChatTimestampsSettingsCard : SettingsCard {
    override val title: String = "Chat Timestamps"
    override val description: String = "Prefixes chat messages with the current time"
    override fun isEnabled(config: HugosmpConfig) = config.chatTimestampsEnabled
    override fun setEnabled(config: HugosmpConfig, enabled: Boolean) {
        config.chatTimestampsEnabled = enabled
    }

    override fun createSettingsScreen(parent: Screen): Screen = ModuleOptionsScreen(
        parent, "Chat Timestamps Settings",
        listOf(
            enabledRow(this),
            ToggleOption("24-Hour Format", { it.chatTimestamps24Hour }, { c, v -> c.chatTimestamps24Hour = v })
        )
    )
}

object ToggleSprintSettingsCard : SettingsCard {
    override val title: String = "Toggle Sprint"
    override val description: String = "Sprint automatically while moving forward"
    override fun isEnabled(config: HugosmpConfig) = config.toggleSprintEnabled
    override fun setEnabled(config: HugosmpConfig, enabled: Boolean) {
        config.toggleSprintEnabled = enabled
    }

    override fun createSettingsScreen(parent: Screen): Screen =
        ModuleOptionsScreen(parent, "Toggle Sprint Settings", listOf(enabledRow(this)))
}

object ToggleSneakSettingsCard : SettingsCard {
    override val title: String = "Toggle Sneak"
    override val description: String = "Press once to stay crouched, press again to stand"
    override fun isEnabled(config: HugosmpConfig) = config.toggleSneakEnabled
    override fun setEnabled(config: HugosmpConfig, enabled: Boolean) {
        config.toggleSneakEnabled = enabled
    }

    override fun createSettingsScreen(parent: Screen): Screen =
        ModuleOptionsScreen(parent, "Toggle Sneak Settings", listOf(enabledRow(this)))
}

object FullbrightSettingsCard : SettingsCard {
    override val title: String = "Fullbright"
    override val description: String = "Removes darkness so caves and nights are fully lit"
    override fun isEnabled(config: HugosmpConfig) = config.fullbrightEnabled
    override fun setEnabled(config: HugosmpConfig, enabled: Boolean) {
        config.fullbrightEnabled = enabled
    }

    override fun createSettingsScreen(parent: Screen): Screen =
        ModuleOptionsScreen(parent, "Fullbright Settings", listOf(enabledRow(this)))
}

object AutoReconnectSettingsCard : SettingsCard {
    override val title: String = "Auto Reconnect"
    override val description: String = "Reconnects after a kick, crash, or lost connection"
    override fun isEnabled(config: HugosmpConfig) = config.autoReconnectEnabled
    override fun setEnabled(config: HugosmpConfig, enabled: Boolean) {
        config.autoReconnectEnabled = enabled
    }

    override fun createSettingsScreen(parent: Screen): Screen = ModuleOptionsScreen(
        parent, "Auto Reconnect Settings",
        listOf(
            enabledRow(this),
            SliderOption(
                "Reconnect Delay", 1f, 30f, { it.autoReconnectDelaySeconds }, { c, v -> c.autoReconnectDelaySeconds = v },
                { "${it.toInt()}s" }
            )
        )
    )
}

object AfkSettingsCard : SettingsCard {
    override val title: String = "AFK Mode"
    override val description: String = "Reconnects and re-sends /afk if the AFK lobby kicks you"
    override fun isEnabled(config: HugosmpConfig) = config.afkModeEnabled
    override fun setEnabled(config: HugosmpConfig, enabled: Boolean) {
        config.afkModeEnabled = enabled
    }

    override fun createSettingsScreen(parent: Screen): Screen = ModuleOptionsScreen(
        parent, "AFK Mode Settings",
        listOf(
            enabledRow(this),
            SliderOption(
                "Resend /afk After", 3f, 30f, { it.afkResendDelaySeconds }, { c, v -> c.afkResendDelaySeconds = v },
                { "${it.toInt()}s" }
            )
        )
    )
}

object ArmorSettingsCard : SettingsCard {
    override val title: String = "Armor HUD"
    override val description: String = "Shows equipped armor and remaining durability"
    override fun isEnabled(config: HugosmpConfig) = config.armorHudEnabled
    override fun setEnabled(config: HugosmpConfig, enabled: Boolean) {
        config.armorHudEnabled = enabled
    }

    override fun createSettingsScreen(parent: Screen): Screen =
        ModuleOptionsScreen(parent, "Armor HUD Settings", listOf(enabledRow(this)))
}

object ItemCounterSettingsCard : SettingsCard {
    override val title: String = "Item Counter"
    override val description: String = "Tracks how many of one item you're carrying"
    override fun isEnabled(config: HugosmpConfig) = config.itemCounterHudEnabled
    override fun setEnabled(config: HugosmpConfig, enabled: Boolean) {
        config.itemCounterHudEnabled = enabled
    }

    override fun createSettingsScreen(parent: Screen): Screen = ModuleOptionsScreen(
        parent, "Item Counter Settings",
        listOf(
            enabledRow(this),
            CycleOption(
                "Tracked Item", ScalableItems.all.map { it.id }, { id -> ScalableItems.all.first { it.id == id }.displayName },
                { it.itemCounterItemId }, { c, v -> c.itemCounterItemId = v }
            )
        )
    )
}

object WaypointsSettingsCard : SettingsCard {
    override val title: String = "Waypoints"
    override val description: String = "Beacon beam + label for death spot and set markers"
    override fun isEnabled(config: HugosmpConfig) = config.waypointsEnabled
    override fun setEnabled(config: HugosmpConfig, enabled: Boolean) {
        config.waypointsEnabled = enabled
    }

    override fun createSettingsScreen(parent: Screen): Screen = ModuleOptionsScreen(
        parent, "Waypoints Settings",
        listOf(
            enabledRow(this),
            ActionOption("Active Waypoints", { it.waypoints.size.toString() }) { }, // read-only count
            ActionOption("Clear All Waypoints", { "Clear" }) { it.waypoints.clear() }
        )
    )
}

object ItemScaleSettingsCard : SettingsCard {
    override val title: String = "Item Scale"
    override val description: String = "Makes chosen items bigger in your inventory and on the ground"
    override fun isEnabled(config: HugosmpConfig) = config.scaledItemIds.isNotEmpty()
    override fun setEnabled(config: HugosmpConfig, enabled: Boolean) {
        if (!enabled) config.scaledItemIds.clear()
    }

    override fun createSettingsScreen(parent: Screen): Screen {
        val itemRows = ScalableItems.all.map { scalable ->
            ItemToggleOption(
                scalable.item, scalable.displayName,
                { config -> scalable.id in config.scaledItemIds },
                { config, enabled ->
                    if (enabled) {
                        if (scalable.id !in config.scaledItemIds) config.scaledItemIds.add(scalable.id)
                    } else {
                        config.scaledItemIds.remove(scalable.id)
                    }
                }
            )
        }
        val rows = listOf<OptionRow>(
            SliderOption("Scale Amount", 1f, 2.5f, { it.itemScaleAmount }, { c, v -> c.itemScaleAmount = v }, { "${"%.1f".format(it)}x" })
        ) + itemRows
        return ModuleOptionsScreen(parent, "Item Scale Settings", rows)
    }
}

object SettingsCards {
    val all: List<SettingsCard> = listOf(
        CoordinatesSettingsCard,
        KeystrokesSettingsCard,
        CpsSettingsCard,
        FpsSettingsCard,
        PingSettingsCard,
        ClockSettingsCard,
        SessionTimerSettingsCard,
        LowHealthAlertSettingsCard,
        DeathCoordsSettingsCard,
        ChatTimestampsSettingsCard,
        ToggleSprintSettingsCard,
        ToggleSneakSettingsCard,
        FullbrightSettingsCard,
        ItemScaleSettingsCard,
        WaypointsSettingsCard,
        ArmorSettingsCard,
        ItemCounterSettingsCard,
        AutoReconnectSettingsCard,
        AfkSettingsCard
    )
}
