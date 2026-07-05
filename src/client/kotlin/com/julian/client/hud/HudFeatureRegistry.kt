package com.julian.client.hud

import com.julian.HugosmpClient
import com.julian.client.config.ConfigManager
import com.julian.client.config.HugosmpConfig
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import kotlin.math.roundToInt

object HudFeatureRegistry {
    val features: List<HudFeature> = listOf(
        CoordinatesHudFeature,
        KeystrokesHudFeature,
        CpsHudFeature,
        FpsHudFeature,
        PingHudFeature,
        ClockHudFeature,
        SessionTimerHudFeature,
        LowHealthAlertHudFeature,
        ArmorHudFeature,
        ItemCounterHudFeature
    )

    fun registerAll() {
        for (feature in features) {
            HudElementRegistry.addLast(HugosmpClient.id(feature.id)) { graphics, deltaTracker ->
                val config = ConfigManager.config
                if (feature.isEnabled(config) && !Minecraft.getInstance().options.hideGui) {
                    renderScaled(graphics, feature, deltaTracker, config)
                }
            }
        }

        ClientTickEvents.END_CLIENT_TICK.register {
            val config = ConfigManager.config
            for (feature in features) {
                if (feature.isEnabled(config)) {
                    feature.tick(config)
                }
            }
        }
    }

    /**
     * Pulls a module's stored position back onto the current screen if it would
     * otherwise render (partially or fully) off-screen - e.g. a saved position
     * from a taller window, or a default that doesn't fit a high GUI scale.
     */
    fun clampedPosition(feature: HudFeature, config: HugosmpConfig, guiWidth: Int, guiHeight: Int): HudPoint {
        val scale = feature.getScale(config)
        val size = feature.measureSize(config)
        val scaledWidth = (size.width * scale).roundToInt()
        val scaledHeight = (size.height * scale).roundToInt()
        val raw = feature.getPosition(config)
        val maxX = (guiWidth - scaledWidth).coerceAtLeast(0)
        val maxY = (guiHeight - scaledHeight).coerceAtLeast(0)
        return HudPoint(raw.x.coerceIn(0, maxX), raw.y.coerceIn(0, maxY))
    }

    /** Scales a feature's rendering around its own anchor and nudges it back on-screen if needed. */
    fun renderScaled(graphics: GuiGraphicsExtractor, feature: HudFeature, deltaTracker: DeltaTracker, config: HugosmpConfig) {
        val scale = feature.getScale(config)
        val rawPos = feature.getPosition(config)
        val clamped = clampedPosition(feature, config, graphics.guiWidth(), graphics.guiHeight())

        val pose = graphics.pose()
        pose.pushMatrix()
        pose.translate((clamped.x - rawPos.x).toFloat(), (clamped.y - rawPos.y).toFloat())
        if (scale != 1f) {
            pose.scaleAround(scale, rawPos.x.toFloat(), rawPos.y.toFloat())
        }
        feature.render(graphics, deltaTracker, config)
        pose.popMatrix()
    }
}
