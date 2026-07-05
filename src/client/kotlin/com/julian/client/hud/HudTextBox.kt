package com.julian.client.hud

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor

/** Shared single-line text-in-a-box look for small readouts (FPS, ping, CPS, clock, session timer). */
internal object HudTextBox {
    /** Box's top-left corner is exactly (x, y) - matters for the HUD editor's border and for scaling around this point. */
    fun draw(graphics: GuiGraphicsExtractor, text: String, x: Int, y: Int, textColor: Int = (0xFF shl 24) or 0xE8EAF0) {
        val font = Minecraft.getInstance().font
        val textWidth = font.width(text)
        graphics.fill(x, y, x + textWidth + 8, y + font.lineHeight + 4, (0x90 shl 24) or 0x1A1D24)
        graphics.text(font, text, x + 4, y + 2, textColor, false)
    }

    fun measure(text: String): HudSize {
        val font = Minecraft.getInstance().font
        return HudSize(font.width(text) + 8, font.lineHeight + 4)
    }
}
