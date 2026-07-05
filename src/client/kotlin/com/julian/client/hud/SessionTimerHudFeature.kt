package com.julian.client.hud

import com.julian.client.config.HugosmpConfig
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphicsExtractor

object SessionTimerHudFeature : HudFeature {
    override val id: String = "session-timer"
    override val displayName: String = "Session Timer"
    override fun isEnabled(config: HugosmpConfig): Boolean = config.sessionTimerHudEnabled

    private val startMillis = System.currentTimeMillis()

    override fun getPosition(config: HugosmpConfig): HudPoint = HudPoint(config.sessionTimerHudX, config.sessionTimerHudY)
    override fun setPosition(config: HugosmpConfig, point: HudPoint) {
        config.sessionTimerHudX = point.x
        config.sessionTimerHudY = point.y
    }

    override fun getScale(config: HugosmpConfig): Float = config.sessionTimerHudScale
    override fun setScale(config: HugosmpConfig, scale: Float) {
        config.sessionTimerHudScale = scale
    }

    override fun measureSize(config: HugosmpConfig): HudSize = HudTextBox.measure("00:00:00")

    override fun render(graphics: GuiGraphicsExtractor, deltaTracker: DeltaTracker, config: HugosmpConfig) {
        val elapsedSeconds = (System.currentTimeMillis() - startMillis) / 1000
        val hours = elapsedSeconds / 3600
        val minutes = (elapsedSeconds % 3600) / 60
        val text = if (config.sessionTimerShowSeconds) {
            "%02d:%02d:%02d".format(hours, minutes, elapsedSeconds % 60)
        } else {
            "%02d:%02d".format(hours, minutes)
        }
        HudTextBox.draw(graphics, text, config.sessionTimerHudX, config.sessionTimerHudY)
    }
}
