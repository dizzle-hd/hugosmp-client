package com.julian.client.gui

import com.julian.client.config.ConfigManager
import com.julian.client.config.HugosmpConfig
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import kotlin.math.roundToInt

/** One configurable row in a [ModuleOptionsScreen]. */
sealed interface OptionRow {
    val label: String
}

class ToggleOption(
    override val label: String,
    val get: (HugosmpConfig) -> Boolean,
    val set: (HugosmpConfig, Boolean) -> Unit
) : OptionRow

class CycleOption<T>(
    override val label: String,
    val values: List<T>,
    val displayName: (T) -> String,
    val get: (HugosmpConfig) -> T,
    val set: (HugosmpConfig, T) -> Unit
) : OptionRow

class SliderOption(
    override val label: String,
    val min: Float,
    val max: Float,
    val get: (HugosmpConfig) -> Float,
    val set: (HugosmpConfig, Float) -> Unit,
    val format: (Float) -> String
) : OptionRow

class ItemToggleOption(
    val item: Item,
    override val label: String,
    val get: (HugosmpConfig) -> Boolean,
    val set: (HugosmpConfig, Boolean) -> Unit
) : OptionRow

/** A clickable action row (e.g. "Clear All") - shows a live value/count and runs a callback on click. */
class ActionOption(
    override val label: String,
    val valueText: (HugosmpConfig) -> String,
    val action: (HugosmpConfig) -> Unit
) : OptionRow

/**
 * A single scrollable settings screen for one module: an "Enabled" toggle
 * plus whatever sliders/cycles/item pickers that module needs. Every module
 * gets one of these behind a left-click on its carousel card - the card face
 * itself is no longer directly interactive.
 */
class ModuleOptionsScreen(
    private val parent: Screen,
    screenTitle: String,
    private val rows: List<OptionRow>
) : Screen(Component.literal(screenTitle)) {

    private val rowHeight = 24
    private val listWidth = 280
    private val listTop = 34
    private var listBottom = 0
    private var scrollOffset = 0

    private var draggingSlider: SliderOption? = null
    private var draggingLeft = 0
    private var draggingWidth = 0

    private fun listLeft() = width / 2 - listWidth / 2
    private fun maxScroll() = (rows.size * rowHeight - (listBottom - listTop)).coerceAtLeast(0)

    override fun init() {
        listBottom = height - 20
        scrollOffset = scrollOffset.coerceIn(0, maxScroll())
    }

    private fun rowScreenTop(index: Int) = listTop + index * rowHeight - scrollOffset

    private fun rowIndexAt(mouseY: Double): Int? {
        if (mouseY < listTop || mouseY >= listBottom) return null
        for (index in rows.indices) {
            val top = rowScreenTop(index)
            if (mouseY >= top && mouseY < top + rowHeight - 2) return index
        }
        return null
    }

    override fun mouseClicked(event: MouseButtonEvent, doubled: Boolean): Boolean {
        val left = listLeft()
        if (event.x() < left || event.x() >= left + listWidth) return super.mouseClicked(event, doubled)
        val index = rowIndexAt(event.y()) ?: return super.mouseClicked(event, doubled)
        val config = ConfigManager.config

        when (val row = rows[index]) {
            is ToggleOption -> {
                row.set(config, !row.get(config))
                ConfigManager.save()
            }
            is ItemToggleOption -> {
                row.set(config, !row.get(config))
                ConfigManager.save()
            }
            is CycleOption<*> -> {
                cycleAdvance(row, config)
                ConfigManager.save()
            }
            is SliderOption -> {
                draggingSlider = row
                draggingLeft = left + 8
                draggingWidth = listWidth - 16
                updateSlider(row, config, event.x())
            }
            is ActionOption -> {
                row.action(config)
                ConfigManager.save()
            }
        }
        return true
    }

    private fun <T> cycleAdvance(row: CycleOption<T>, config: HugosmpConfig) {
        val i = row.values.indexOf(row.get(config)).let { if (it < 0) 0 else it }
        row.set(config, row.values[(i + 1) % row.values.size])
    }

    private fun updateSlider(row: SliderOption, config: HugosmpConfig, mouseX: Double) {
        val fraction = ((mouseX - draggingLeft) / draggingWidth).coerceIn(0.0, 1.0)
        row.set(config, row.min + (row.max - row.min) * fraction.toFloat())
    }

    override fun mouseDragged(event: MouseButtonEvent, dragX: Double, dragY: Double): Boolean {
        val row = draggingSlider ?: return super.mouseDragged(event, dragX, dragY)
        updateSlider(row, ConfigManager.config, event.x())
        return true
    }

    override fun mouseReleased(event: MouseButtonEvent): Boolean {
        if (draggingSlider != null) {
            draggingSlider = null
            ConfigManager.save()
            return true
        }
        return super.mouseReleased(event)
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
        val config = ConfigManager.config

        graphics.enableScissor(left, listTop, left + listWidth, listBottom)
        for (index in rows.indices) {
            val rowTop = rowScreenTop(index)
            if (rowTop + rowHeight < listTop || rowTop > listBottom) continue
            drawRow(graphics, rows[index], left, rowTop, config)
        }
        graphics.disableScissor()

        if (maxScroll() > 0) {
            graphics.centeredText(font, "Scroll for more", width / 2, listBottom + 2, (0xFF shl 24) or 0x8A8F9C)
        } else {
            graphics.centeredText(font, "Esc to go back", width / 2, listBottom + 2, (0xFF shl 24) or 0x8A8F9C)
        }
    }

    private fun drawRow(graphics: GuiGraphicsExtractor, row: OptionRow, left: Int, top: Int, config: HugosmpConfig) {
        graphics.fill(left, top, left + listWidth, top + rowHeight - 2, (0x30 shl 24) or 0x1A1D24)
        graphics.outline(left, top, listWidth, rowHeight - 2, (0xA0 shl 24) or 0x3A3F4B)
        val textY = top + (rowHeight - 2) / 2 - font.lineHeight / 2

        when (row) {
            is ToggleOption -> {
                graphics.text(font, row.label, left + 8, textY, (0xFF shl 24) or 0xE8EAF0, false)
                drawValue(graphics, if (row.get(config)) "On" else "Off", left, top)
            }
            is CycleOption<*> -> {
                graphics.text(font, row.label, left + 8, textY, (0xFF shl 24) or 0xE8EAF0, false)
                drawValue(graphics, cycleText(row, config), left, top)
            }
            is ItemToggleOption -> {
                graphics.item(ItemStack(row.item), left + 6, top + 3)
                graphics.text(font, row.label, left + 28, textY, (0xFF shl 24) or 0xE8EAF0, false)
                drawValue(graphics, if (row.get(config)) "On" else "Off", left, top)
            }
            is SliderOption -> {
                val fraction = ((row.get(config) - row.min) / (row.max - row.min)).coerceIn(0f, 1f)
                val filledWidth = ((listWidth - 16) * fraction).roundToInt().coerceAtLeast(1)
                graphics.fill(left + 8, top + 2, left + 8 + filledWidth, top + rowHeight - 4, (0xB0 shl 24) or 0x4C6FCC)
                graphics.text(font, row.label, left + 8, textY, (0xFF shl 24) or 0xE8EAF0, false)
                drawValue(graphics, row.format(row.get(config)), left, top)
            }
            is ActionOption -> {
                graphics.text(font, row.label, left + 8, textY, (0xFF shl 24) or 0xE8EAF0, false)
                drawValue(graphics, row.valueText(config), left, top)
            }
        }
    }

    private fun <T> cycleText(row: CycleOption<T>, config: HugosmpConfig): String = row.displayName(row.get(config))

    private fun drawValue(graphics: GuiGraphicsExtractor, text: String, left: Int, top: Int) {
        val valueX = left + listWidth - 8 - font.width(text)
        val textY = top + (rowHeight - 2) / 2 - font.lineHeight / 2
        graphics.text(font, text, valueX, textY, (0xFF shl 24) or 0x7AA2FF, false)
    }
}
