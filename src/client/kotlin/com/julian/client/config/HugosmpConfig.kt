package com.julian.client.config

/**
 * Flat, feature-prefixed settings. Kept flat rather than nested: Gson
 * instantiates this via reflection, so a field missing from an older
 * config file is simply left at its Kotlin default here - a nested
 * reference type would instead risk being left null.
 *
 * All modules are disabled by default - nothing renders or activates until
 * the player opts in via the settings carousel.
 */
data class HugosmpConfig(
    var coordsHudEnabled: Boolean = false,
    var coordsHudX: Int = 4,
    var coordsHudY: Int = 4,
    var coordsHudScale: Float = 1f,
    var coordsHudShowBiome: Boolean = false,
    var coordsHudTextColor: Int = 0xE0E0E0,
    var coordsHudBackgroundAlpha: Int = 90,

    var keystrokesHudEnabled: Boolean = false,
    var keystrokesHudX: Int = 4,
    var keystrokesHudY: Int = 140,
    var keystrokesHudScale: Float = 1f,
    var keystrokesShowMouseButtons: Boolean = true,

    var cpsHudEnabled: Boolean = false,
    var cpsHudX: Int = 4,
    var cpsHudY: Int = 60,
    var cpsHudScale: Float = 1f,
    var cpsShowPeak: Boolean = false,

    var fpsHudEnabled: Boolean = false,
    var fpsHudX: Int = 4,
    var fpsHudY: Int = 76,
    var fpsHudScale: Float = 1f,
    var fpsWarnThreshold: Float = 30f,

    var pingHudEnabled: Boolean = false,
    var pingHudX: Int = 4,
    var pingHudY: Int = 92,
    var pingHudScale: Float = 1f,
    var pingWarnThreshold: Float = 150f,

    var clockHudEnabled: Boolean = false,
    var clockHudX: Int = 4,
    var clockHudY: Int = 108,
    var clockHudScale: Float = 1f,
    var clockUse24Hour: Boolean = true,

    var sessionTimerHudEnabled: Boolean = false,
    var sessionTimerHudX: Int = 4,
    var sessionTimerHudY: Int = 124,
    var sessionTimerHudScale: Float = 1f,
    var sessionTimerShowSeconds: Boolean = true,

    var lowHealthAlertEnabled: Boolean = false,
    var lowHealthThreshold: Float = 6f,
    var lowHealthAlertX: Int = 4,
    var lowHealthAlertY: Int = 156,
    var lowHealthAlertScale: Float = 1f,

    var deathCoordsEnabled: Boolean = false,
    var deathCoordsIncludeDimension: Boolean = true,

    var chatTimestampsEnabled: Boolean = false,
    var chatTimestamps24Hour: Boolean = true,

    var toggleSprintEnabled: Boolean = false,
    var toggleSneakEnabled: Boolean = false,

    var fullbrightEnabled: Boolean = false,
    var itemScaleAmount: Float = 1.8f,
    var scaledItemIds: MutableList<String> = mutableListOf(),

    var waypointsEnabled: Boolean = false,
    var waypoints: MutableList<Waypoint> = mutableListOf(),

    var armorHudEnabled: Boolean = false,
    var armorHudX: Int = 4,
    var armorHudY: Int = 172,
    var armorHudScale: Float = 1f,

    var itemCounterHudEnabled: Boolean = false,
    var itemCounterHudX: Int = 4,
    var itemCounterHudY: Int = 188,
    var itemCounterHudScale: Float = 1f,
    var itemCounterItemId: String = "minecraft:totem_of_undying",

    var autoReconnectEnabled: Boolean = false,
    var autoReconnectDelaySeconds: Float = 5f,

    var afkModeEnabled: Boolean = false,
    var afkResendDelaySeconds: Float = 10f
)
