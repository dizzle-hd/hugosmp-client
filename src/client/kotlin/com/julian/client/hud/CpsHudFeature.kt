package com.julian.client.hud

import com.julian.client.config.HugosmpConfig
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor

/** Counts left-click presses in the last second - purely informational, no automation of clicks. */
object CpsHudFeature : HudFeature {
    override val id: String = "cps"
    override val displayName: String = "CPS Counter"
    override fun isEnabled(config: HugosmpConfig): Boolean = config.cpsHudEnabled

    private var wasDown = false
    private val clickTimestamps = ArrayDeque<Long>()
    private var peak = 0

    override fun getPosition(config: HugosmpConfig): HudPoint = HudPoint(config.cpsHudX, config.cpsHudY)
    override fun setPosition(config: HugosmpConfig, point: HudPoint) {
        config.cpsHudX = point.x
        config.cpsHudY = point.y
    }

    override fun getScale(config: HugosmpConfig): Float = config.cpsHudScale
    override fun setScale(config: HugosmpConfig, scale: Float) {
        config.cpsHudScale = scale
    }

    override fun measureSize(config: HugosmpConfig): HudSize = HudTextBox.measure("99 CPS (peak 99)")

    override fun tick(config: HugosmpConfig) {
        val isDown = Minecraft.getInstance().options.keyAttack.isDown
        if (isDown && !wasDown) {
            clickTimestamps.addLast(System.currentTimeMillis())
        }
        wasDown = isDown
    }

    override fun render(graphics: GuiGraphicsExtractor, deltaTracker: DeltaTracker, config: HugosmpConfig) {
        val now = System.currentTimeMillis()
        while (clickTimestamps.isNotEmpty() && now - clickTimestamps.first() > 1000) {
            clickTimestamps.removeFirst()
        }
        val current = clickTimestamps.size
        if (current > peak) peak = current

        val text = if (config.cpsShowPeak) "$current CPS (peak $peak)" else "$current CPS"
        HudTextBox.draw(graphics, text, config.cpsHudX, config.cpsHudY)
    }
}
