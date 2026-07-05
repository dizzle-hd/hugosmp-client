package com.julian.client.hud

import com.julian.client.config.HugosmpConfig
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor

/** Shows WASD and mouse buttons as small squares that light up while held - purely cosmetic/informational. */
object KeystrokesHudFeature : HudFeature {
    override val id: String = "keystrokes"
    override val displayName: String = "Keystrokes"
    override fun isEnabled(config: HugosmpConfig): Boolean = config.keystrokesHudEnabled

    private const val KEY_SIZE = 16
    private const val GAP = 2
    private const val STEP = KEY_SIZE + GAP

    override fun getPosition(config: HugosmpConfig): HudPoint = HudPoint(config.keystrokesHudX, config.keystrokesHudY)
    override fun setPosition(config: HugosmpConfig, point: HudPoint) {
        config.keystrokesHudX = point.x
        config.keystrokesHudY = point.y
    }

    override fun getScale(config: HugosmpConfig): Float = config.keystrokesHudScale
    override fun setScale(config: HugosmpConfig, scale: Float) {
        config.keystrokesHudScale = scale
    }

    override fun measureSize(config: HugosmpConfig): HudSize =
        HudSize(STEP * 3, if (config.keystrokesShowMouseButtons) STEP * 3 else STEP * 2)

    override fun render(graphics: GuiGraphicsExtractor, deltaTracker: DeltaTracker, config: HugosmpConfig) {
        val options = Minecraft.getInstance().options
        val originX = config.keystrokesHudX
        val originY = config.keystrokesHudY

        drawKey(graphics, originX + STEP, originY, "W", options.keyUp.isDown)
        drawKey(graphics, originX, originY + STEP, "A", options.keyLeft.isDown)
        drawKey(graphics, originX + STEP, originY + STEP, "S", options.keyDown.isDown)
        drawKey(graphics, originX + STEP * 2, originY + STEP, "D", options.keyRight.isDown)

        if (config.keystrokesShowMouseButtons) {
            val row = originY + STEP * 2
            drawKey(graphics, originX, row, "L", options.keyAttack.isDown)
            drawKey(graphics, originX + STEP * 2, row, "R", options.keyUse.isDown)
        }
    }

    private fun drawKey(graphics: GuiGraphicsExtractor, x: Int, y: Int, label: String, pressed: Boolean) {
        val font = Minecraft.getInstance().font
        val background = if (pressed) (0xD0 shl 24) or 0x4C6FCC else (0xA0 shl 24) or 0x1A1D24
        graphics.fill(x, y, x + KEY_SIZE, y + KEY_SIZE, background)
        graphics.outline(x, y, KEY_SIZE, KEY_SIZE, (0xD0 shl 24) or 0x50545F)

        val textX = x + KEY_SIZE / 2 - font.width(label) / 2
        val textY = y + KEY_SIZE / 2 - font.lineHeight / 2
        graphics.text(font, label, textX, textY, (0xFF shl 24) or 0xE8EAF0, false)
    }
}
