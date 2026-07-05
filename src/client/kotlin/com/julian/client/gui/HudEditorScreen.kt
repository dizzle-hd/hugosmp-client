package com.julian.client.gui

import com.julian.client.config.ConfigManager
import com.julian.client.config.HugosmpConfig
import com.julian.client.hud.HudFeature
import com.julian.client.hud.HudFeatureRegistry
import com.julian.client.hud.HudPoint
import com.julian.client.hud.HudSize
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import kotlin.math.roundToInt

/**
 * Shows every enabled HUD module at its real position, over the actual game
 * (no dimming) so positioning is WYSIWYG - drag a module's outlined box to
 * move it, scroll over it to resize it, same interaction as Lunar/Badlion's
 * HUD editors.
 */
class HudEditorScreen(private val parent: Screen? = null) : Screen(Component.literal("HUD Editor")) {

    private data class DragState(val feature: HudFeature, val grabDx: Int, val grabDy: Int)
    private var dragState: DragState? = null

    private val minScale = 0.5f
    private val maxScale = 3f

    private fun visiblePosition(feature: HudFeature, config: HugosmpConfig): HudPoint =
        HudFeatureRegistry.clampedPosition(feature, config, width, height)

    private fun scaledSize(feature: HudFeature, config: HugosmpConfig): HudSize {
        val base = feature.measureSize(config)
        val scale = feature.getScale(config)
        return HudSize((base.width * scale).roundToInt(), (base.height * scale).roundToInt())
    }

    private fun featureUnderMouse(mouseX: Double, mouseY: Double): HudFeature? {
        val config = ConfigManager.config
        for (feature in HudFeatureRegistry.features) {
            if (!feature.isEnabled(config)) continue
            val pos = visiblePosition(feature, config)
            val size = scaledSize(feature, config)
            if (mouseX >= pos.x && mouseX < pos.x + size.width && mouseY >= pos.y && mouseY < pos.y + size.height) {
                return feature
            }
        }
        return null
    }

    override fun mouseClicked(event: MouseButtonEvent, doubled: Boolean): Boolean {
        val config = ConfigManager.config
        val feature = featureUnderMouse(event.x(), event.y())
        if (feature != null) {
            val pos = visiblePosition(feature, config)
            dragState = DragState(feature, event.x().toInt() - pos.x, event.y().toInt() - pos.y)
            return true
        }
        return super.mouseClicked(event, doubled)
    }

    override fun mouseDragged(event: MouseButtonEvent, dragX: Double, dragY: Double): Boolean {
        val state = dragState ?: return super.mouseDragged(event, dragX, dragY)
        val config = ConfigManager.config
        val size = scaledSize(state.feature, config)
        val newX = (event.x().toInt() - state.grabDx).coerceIn(0, (width - size.width).coerceAtLeast(0))
        val newY = (event.y().toInt() - state.grabDy).coerceIn(0, (height - size.height).coerceAtLeast(0))
        state.feature.setPosition(config, HudPoint(newX, newY))
        return true
    }

    override fun mouseReleased(event: MouseButtonEvent): Boolean {
        if (dragState != null) {
            dragState = null
            ConfigManager.save()
            return true
        }
        return super.mouseReleased(event)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        if (scrollY == 0.0) return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
        val feature = featureUnderMouse(mouseX, mouseY) ?: return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
        val config = ConfigManager.config
        val newScale = (feature.getScale(config) + if (scrollY > 0) 0.1f else -0.1f).coerceIn(minScale, maxScale)
        feature.setScale(config, newScale)
        ConfigManager.save()
        return true
    }

    override fun onClose() {
        ConfigManager.save()
        minecraft.setScreen(parent)
    }

    override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
        val config = ConfigManager.config
        val enabled = HudFeatureRegistry.features.filter { it.isEnabled(config) }

        graphics.fill(0, 0, width, 18, 0x90 shl 24)
        graphics.centeredText(font, title, width / 2, 5, (0xFF shl 24) or 0xE0E0E0)

        if (enabled.isEmpty()) {
            graphics.centeredText(font, "No modules enabled yet.", width / 2, height / 2 - 10, (0xFF shl 24) or 0xA0A0A0)
            graphics.centeredText(
                font, "Enable some in HugoSMP Settings first.", width / 2, height / 2 + 4,
                (0xFF shl 24) or 0x8A8F9C
            )
        }

        for (feature in enabled) {
            HudFeatureRegistry.renderScaled(graphics, feature, DeltaTracker.ZERO, config)

            val pos = visiblePosition(feature, config)
            val size = scaledSize(feature, config)
            val dragging = dragState?.feature === feature
            val borderColor = if (dragging) (0xFF shl 24) or 0x7AA2FF else (0x90 shl 24) or 0x7AA2FF
            graphics.outline(pos.x - 2, pos.y - 2, size.width + 4, size.height + 4, borderColor)

            val label = "${feature.displayName} (${"%.1f".format(feature.getScale(config))}x)"
            graphics.centeredText(
                font, label, pos.x + size.width / 2, pos.y - font.lineHeight - 4,
                (0xFF shl 24) or 0xE8EAF0
            )
        }

        graphics.fill(0, height - 16, width, height, 0x90 shl 24)
        graphics.centeredText(
            font, "Drag to move - scroll over a module to resize - Esc to close",
            width / 2, height - 12, (0xFF shl 24) or 0x8A8F9C
        )
    }
}
