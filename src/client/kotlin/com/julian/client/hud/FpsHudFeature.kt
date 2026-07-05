package com.julian.client.hud

import com.julian.client.config.HugosmpConfig
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor

object FpsHudFeature : HudFeature {
    override val id: String = "fps"
    override val displayName: String = "FPS Counter"
    override fun isEnabled(config: HugosmpConfig): Boolean = config.fpsHudEnabled

    override fun getPosition(config: HugosmpConfig): HudPoint = HudPoint(config.fpsHudX, config.fpsHudY)
    override fun setPosition(config: HugosmpConfig, point: HudPoint) {
        config.fpsHudX = point.x
        config.fpsHudY = point.y
    }

    override fun getScale(config: HugosmpConfig): Float = config.fpsHudScale
    override fun setScale(config: HugosmpConfig, scale: Float) {
        config.fpsHudScale = scale
    }

    override fun measureSize(config: HugosmpConfig): HudSize = HudTextBox.measure("999 FPS")

    override fun render(graphics: GuiGraphicsExtractor, deltaTracker: DeltaTracker, config: HugosmpConfig) {
        val fps = Minecraft.getInstance().fps
        val color = if (fps < config.fpsWarnThreshold) (0xFF shl 24) or 0xFF5C5C else (0xFF shl 24) or 0xE8EAF0
        HudTextBox.draw(graphics, "$fps FPS", config.fpsHudX, config.fpsHudY, color)
    }
}
