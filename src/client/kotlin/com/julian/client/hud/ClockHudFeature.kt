package com.julian.client.hud

import com.julian.client.config.HugosmpConfig
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphicsExtractor
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object ClockHudFeature : HudFeature {
    override val id: String = "clock"
    override val displayName: String = "Clock"
    override fun isEnabled(config: HugosmpConfig): Boolean = config.clockHudEnabled

    private val formatter24 = DateTimeFormatter.ofPattern("HH:mm:ss")
    private val formatter12 = DateTimeFormatter.ofPattern("hh:mm:ss a")

    override fun getPosition(config: HugosmpConfig): HudPoint = HudPoint(config.clockHudX, config.clockHudY)
    override fun setPosition(config: HugosmpConfig, point: HudPoint) {
        config.clockHudX = point.x
        config.clockHudY = point.y
    }

    override fun getScale(config: HugosmpConfig): Float = config.clockHudScale
    override fun setScale(config: HugosmpConfig, scale: Float) {
        config.clockHudScale = scale
    }

    override fun measureSize(config: HugosmpConfig): HudSize = HudTextBox.measure("00:00:00 AM")

    override fun render(graphics: GuiGraphicsExtractor, deltaTracker: DeltaTracker, config: HugosmpConfig) {
        val formatter = if (config.clockUse24Hour) formatter24 else formatter12
        HudTextBox.draw(graphics, LocalTime.now().format(formatter), config.clockHudX, config.clockHudY)
    }
}
