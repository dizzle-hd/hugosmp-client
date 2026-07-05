package com.julian.client.gui

import com.julian.client.config.ConfigManager
import com.julian.client.config.Waypoint
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import kotlin.math.roundToInt

/**
 * A dedicated, always-current list of every waypoint - opened directly via
 * its own keybind rather than through the settings carousel, since the
 * number of rows changes live as waypoints are added/removed.
 */
class WaypointManagerScreen(private val parent: Screen? = null) : Screen(Component.literal("Waypoints")) {

    private val rowHeight = 26
    private val listWidth = 280
    private val listTop = 34
    private var listBottom = 0
    private var scrollOffset = 0

    private val swatchSize = 12
    private val removeText = "Remove"

    private fun listLeft() = width / 2 - listWidth / 2
    private fun waypoints(): MutableList<Waypoint> = ConfigManager.config.waypoints
    private fun maxScroll() = (waypoints().size * rowHeight - (listBottom - listTop)).coerceAtLeast(0)

    override fun init() {
        listBottom = height - 20
        scrollOffset = scrollOffset.coerceIn(0, maxScroll())
    }

    private fun rowScreenTop(index: Int) = listTop + index * rowHeight - scrollOffset

    override fun mouseClicked(event: MouseButtonEvent, doubled: Boolean): Boolean {
        val left = listLeft()
        val list = waypoints()
        if (event.x() < left || event.x() >= left + listWidth) return super.mouseClicked(event, doubled)

        for (index in list.indices) {
            val top = rowScreenTop(index)
            if (top + rowHeight - 2 < listTop || top > listBottom) continue
            if (event.y() < top || event.y() >= top + rowHeight - 2) continue

            val removeLeft = left + listWidth - 8 - font.width(removeText)
            if (event.x() >= removeLeft) {
                waypoints().removeAt(index)
                ConfigManager.save()
                scrollOffset = scrollOffset.coerceIn(0, maxScroll())
                return true
            }
            return true
        }
        return super.mouseClicked(event, doubled)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        if (scrollY == 0.0) return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
        scrollOffset = (scrollOffset - (scrollY * rowHeight).roundToInt()).coerceIn(0, maxScroll())
        return true
    }

    override fun onClose() {
        ConfigManager.save()
        minecraft.setScreen(parent)
    }

    override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
        graphics.fill(0, 0, width, height, 0xC8 shl 24)
        graphics.centeredText(font, title, width / 2, 12, (0xFF shl 24) or 0xE0E0E0)

        val left = listLeft()
        val list = waypoints()

        if (list.isEmpty()) {
            graphics.centeredText(font, "No waypoints set.", width / 2, height / 2 - 6, (0xFF shl 24) or 0xA0A0A0)
            graphics.centeredText(
                font, "Press the Set Waypoint keybind to add one.", width / 2, height / 2 + 8,
                (0xFF shl 24) or 0x8A8F9C
            )
        } else {
            graphics.enableScissor(left, listTop, left + listWidth, listBottom)
            for (index in list.indices) {
                val top = rowScreenTop(index)
                if (top + rowHeight < listTop || top > listBottom) continue
                drawRow(graphics, list[index], left, top)
            }
            graphics.disableScissor()
        }

        val hint = if (maxScroll() > 0) "Scroll for more - Esc to close" else "Esc to close"
        graphics.centeredText(font, hint, width / 2, listBottom + 2, (0xFF shl 24) or 0x8A8F9C)
    }

    private fun drawRow(graphics: GuiGraphicsExtractor, waypoint: Waypoint, left: Int, top: Int) {
        graphics.fill(left, top, left + listWidth, top + rowHeight - 2, (0x30 shl 24) or 0x1A1D24)
        graphics.outline(left, top, listWidth, rowHeight - 2, (0xA0 shl 24) or 0x3A3F4B)

        val swatchY = top + (rowHeight - 2) / 2 - swatchSize / 2
        graphics.fill(left + 8, swatchY, left + 8 + swatchSize, swatchY + swatchSize, (0xFF shl 24) or (waypoint.colorRGB and 0xFFFFFF))
        graphics.outline(left + 8, swatchY, swatchSize, swatchSize, (0xFF shl 24) or 0x50545F)

        val textY = top + (rowHeight - 2) / 2 - font.lineHeight / 2
        graphics.text(font, waypoint.label, left + 8 + swatchSize + 6, textY, (0xFF shl 24) or 0xE8EAF0, false)

        val removeX = left + listWidth - 8 - font.width(removeText)
        graphics.text(font, removeText, removeX, textY, (0xFF shl 24) or 0xFF5C5C, false)
    }
}
