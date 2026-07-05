package com.julian.client.hud

import com.julian.client.config.HugosmpConfig
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor

/**
 * A small, static-color coordinates overlay - deliberately no color cycling,
 * watermark or arraylist styling, to read as a normal client feature rather
 * than a cheat-client HUD.
 */
object CoordinatesHudFeature : HudFeature {
    override val id: String = "coordinates"
    override val displayName: String = "Coordinates"

    override fun isEnabled(config: HugosmpConfig): Boolean = config.coordsHudEnabled

    override fun getPosition(config: HugosmpConfig): HudPoint = HudPoint(config.coordsHudX, config.coordsHudY)
    override fun setPosition(config: HugosmpConfig, point: HudPoint) {
        config.coordsHudX = point.x
        config.coordsHudY = point.y
    }

    override fun getScale(config: HugosmpConfig): Float = config.coordsHudScale
    override fun setScale(config: HugosmpConfig, scale: Float) {
        config.coordsHudScale = scale
    }

    private fun currentLines(config: HugosmpConfig): List<String> {
        val player = Minecraft.getInstance().player ?: return listOf("XYZ: 0 / 0 / 0", "Facing: North")
        val pos = player.blockPosition()
        val lines = mutableListOf(
            "XYZ: ${pos.x} / ${pos.y} / ${pos.z}",
            "Facing: ${player.direction.serializedName.replaceFirstChar { it.uppercase() }}"
        )
        if (config.coordsHudShowBiome) {
            val biomeName = player.level().getBiome(pos).unwrapKey()
                .map { it.identifier().path }
                .orElse("unknown")
            lines += "Biome: $biomeName"
        }
        return lines
    }

    private fun boxSize(lines: List<String>): HudSize {
        val font = Minecraft.getInstance().font
        val padding = 4
        val lineHeight = font.lineHeight + 1
        return HudSize(lines.maxOf { font.width(it) } + padding * 2, lines.size * lineHeight + padding)
    }

    /** Shared exactly with [render] so the HUD editor's box always matches what actually gets drawn. */
    override fun measureSize(config: HugosmpConfig): HudSize = boxSize(currentLines(config))

    override fun render(graphics: GuiGraphicsExtractor, deltaTracker: DeltaTracker, config: HugosmpConfig) {
        val mc = Minecraft.getInstance()
        if (mc.player == null) return
        if (mc.gui.debugOverlay.showDebugScreen()) return

        val lines = currentLines(config)
        val size = boxSize(lines)
        val font = mc.font
        val padding = 4
        val lineHeight = font.lineHeight + 1

        val x = config.coordsHudX
        val y = config.coordsHudY
        val backgroundColor = ((config.coordsHudBackgroundAlpha and 0xFF) shl 24)
        graphics.fill(x, y, x + size.width, y + size.height, backgroundColor)

        val textColor = (0xFF shl 24) or (config.coordsHudTextColor and 0xFFFFFF)
        lines.forEachIndexed { index, line ->
            graphics.text(font, line, x + padding, y + padding + index * lineHeight, textColor, false)
        }
    }
}
