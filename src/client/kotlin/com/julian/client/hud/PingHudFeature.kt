package com.julian.client.hud

import com.julian.client.config.HugosmpConfig
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor

object PingHudFeature : HudFeature {
    override val id: String = "ping"
    override val displayName: String = "Ping"
    override fun isEnabled(config: HugosmpConfig): Boolean = config.pingHudEnabled

    override fun getPosition(config: HugosmpConfig): HudPoint = HudPoint(config.pingHudX, config.pingHudY)
    override fun setPosition(config: HugosmpConfig, point: HudPoint) {
        config.pingHudX = point.x
        config.pingHudY = point.y
    }

    override fun getScale(config: HugosmpConfig): Float = config.pingHudScale
    override fun setScale(config: HugosmpConfig, scale: Float) {
        config.pingHudScale = scale
    }

    override fun measureSize(config: HugosmpConfig): HudSize = HudTextBox.measure("9999 ms")

    override fun render(graphics: GuiGraphicsExtractor, deltaTracker: DeltaTracker, config: HugosmpConfig) {
        val mc = Minecraft.getInstance()
        val player = mc.player ?: return
        val ping = mc.connection?.getPlayerInfo(player.uuid)?.latency ?: 0
        val color = if (ping > config.pingWarnThreshold) (0xFF shl 24) or 0xFF5C5C else (0xFF shl 24) or 0xE8EAF0
        HudTextBox.draw(graphics, "$ping ms", config.pingHudX, config.pingHudY, color)
    }
}
