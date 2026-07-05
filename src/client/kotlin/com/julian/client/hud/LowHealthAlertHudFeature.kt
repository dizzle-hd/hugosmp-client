package com.julian.client.hud

import com.julian.client.config.HugosmpConfig
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import kotlin.math.sin

/** Pulses a warning label when health drops below the configured threshold. Purely visual, no auto-actions. */
object LowHealthAlertHudFeature : HudFeature {
    override val id: String = "low-health-alert"
    override val displayName: String = "Low Health Alert"
    override fun isEnabled(config: HugosmpConfig): Boolean = config.lowHealthAlertEnabled

    override fun getPosition(config: HugosmpConfig): HudPoint = HudPoint(config.lowHealthAlertX, config.lowHealthAlertY)
    override fun setPosition(config: HugosmpConfig, point: HudPoint) {
        config.lowHealthAlertX = point.x
        config.lowHealthAlertY = point.y
    }

    override fun getScale(config: HugosmpConfig): Float = config.lowHealthAlertScale
    override fun setScale(config: HugosmpConfig, scale: Float) {
        config.lowHealthAlertScale = scale
    }

    override fun measureSize(config: HugosmpConfig): HudSize = HudSize(Minecraft.getInstance().font.width("LOW HEALTH") + 8, 12)

    override fun render(graphics: GuiGraphicsExtractor, deltaTracker: DeltaTracker, config: HugosmpConfig) {
        val mc = Minecraft.getInstance()
        val player = mc.player ?: return
        if (player.health > config.lowHealthThreshold) return

        val pulse = sin(System.currentTimeMillis() / 150.0) * 0.5 + 0.5
        val alpha = (120 + pulse * 100).toInt().coerceIn(0, 255)
        graphics.text(mc.font, "LOW HEALTH", config.lowHealthAlertX, config.lowHealthAlertY, (alpha shl 24) or 0xFF5C5C, false)
    }
}
