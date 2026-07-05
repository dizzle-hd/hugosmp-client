package com.julian.client.gui

import com.julian.client.config.ConfigManager
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.CharacterEvent
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import org.lwjgl.glfw.GLFW
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.roundToInt

/**
 * A horizontal settings carousel: cards are laid out in a row, the one
 * closest to the scroll position is largest and fully interactive, cards
 * further away shrink (capped at distance 3) and dim. Every card is drawn at
 * a fixed logical size and then scaled as a whole via the pose stack, so text
 * shrinks together with the card instead of overflowing it. Clicking a card's
 * toggle switches it on/off directly; clicking anywhere else on the focused
 * card opens its full settings screen. A search box is focused the moment
 * this screen opens - just start typing to jump to a module. Deliberately
 * flat/bordered/muted rather than a neon "hack-client" ClickGUI.
 */
class HugosmpConfigScreen(private val parent: Screen?) :
    Screen(Component.translatable("hugosmp-client.settings.title")) {

    private val cards = SettingsCards.all

    private var focusIndex = 0f
    private var targetIndex = 0
    private var lastFrameNanos = System.nanoTime()

    private var searchQuery = ""

    private val cardSize = 130
    private val slotSpacing = 150
    private val textMargin = 18
    private val toggleWidth = 34
    private val toggleHeight = 16
    private val maxDistance = 3f
    private val scalePerStep = 0.15f

    private val searchBoxWidth = 200
    private val searchBoxHeight = 16
    private val searchBoxTop = 32
    private fun searchBoxLeft() = width / 2 - searchBoxWidth / 2

    override fun init() {
        lastFrameNanos = System.nanoTime()
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        if (cards.isEmpty() || scrollY == 0.0) return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
        val direction = if (scrollY > 0) -1 else 1
        targetIndex = (targetIndex + direction).coerceIn(0, cards.size - 1)
        return true
    }

    override fun charTyped(event: CharacterEvent): Boolean {
        if (!event.isAllowedChatCharacter) return super.charTyped(event)
        searchQuery += event.codepointAsString()
        jumpToFirstMatch()
        return true
    }

    override fun keyPressed(event: KeyEvent): Boolean {
        if (event.key() == GLFW.GLFW_KEY_BACKSPACE && searchQuery.isNotEmpty()) {
            searchQuery = searchQuery.dropLast(1)
            jumpToFirstMatch()
            return true
        }
        return super.keyPressed(event)
    }

    private fun jumpToFirstMatch() {
        if (searchQuery.isBlank()) return
        val index = cards.indexOfFirst { it.title.contains(searchQuery, ignoreCase = true) }
        if (index >= 0) targetIndex = index
    }

    private val hudEditorLinkText = "HUD Editor >"
    private fun hudEditorLinkLeft() = width - font.width(hudEditorLinkText) - 16
    private val hudEditorLinkTop = 6

    override fun mouseClicked(event: MouseButtonEvent, doubled: Boolean): Boolean {
        val linkLeft = hudEditorLinkLeft()
        val linkWidth = font.width(hudEditorLinkText) + 8
        val linkHeight = font.lineHeight + 6
        if (event.x() >= linkLeft && event.x() < linkLeft + linkWidth &&
            event.y() >= hudEditorLinkTop && event.y() < hudEditorLinkTop + linkHeight
        ) {
            minecraft.setScreen(HudEditorScreen(this))
            return true
        }

        if (cards.isEmpty()) return super.mouseClicked(event, doubled)
        val centerX = width / 2
        val centerY = height / 2
        val frontIndex = focusIndex.roundToInt().coerceIn(0, cards.size - 1)

        for (index in cards.indices) {
            val distance = index - focusIndex
            val scale = scaleForDistance(distance)
            val size = (cardSize * scale).roundToInt()
            val x = (centerX + distance * slotSpacing).roundToInt()
            val left = x - size / 2
            val top = centerY - size / 2

            if (event.x() < left || event.x() >= left + size || event.y() < top || event.y() >= top + size) continue

            if (index == frontIndex) {
                val card = cards[index]
                val toggleLeft = x - toggleWidth / 2
                val toggleTop = top + size - 30
                if (event.x() >= toggleLeft && event.x() < toggleLeft + toggleWidth &&
                    event.y() >= toggleTop && event.y() < toggleTop + toggleHeight
                ) {
                    val config = ConfigManager.config
                    card.setEnabled(config, !card.isEnabled(config))
                    ConfigManager.save()
                    return true
                }
                minecraft.setScreen(card.createSettingsScreen(this))
                return true
            }
            targetIndex = index
            return true
        }
        return super.mouseClicked(event, doubled)
    }

    override fun onClose() {
        ConfigManager.save()
        minecraft.setScreen(parent)
    }

    override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
        advanceAnimation()

        graphics.fill(0, 0, width, height, 0xB2 shl 24)
        graphics.centeredText(font, title, width / 2, 20, (0xFF shl 24) or 0xE0E0E0)
        graphics.text(font, hudEditorLinkText, hudEditorLinkLeft(), hudEditorLinkTop + 3, (0xFF shl 24) or 0x7AA2FF, false)
        drawSearchBox(graphics)

        if (cards.isEmpty()) {
            graphics.centeredText(font, "No settings yet.", width / 2, height / 2, (0xFF shl 24) or 0xA0A0A0)
            return
        }

        val centerX = width / 2
        val centerY = height / 2

        // Draw back-to-front so the focused (largest) card ends up on top.
        for (index in cards.indices.sortedByDescending { abs(it - focusIndex) }) {
            drawCard(graphics, index, index - focusIndex, centerX, centerY)
        }

        graphics.centeredText(
            font, "Scroll or type to search - click to toggle - Esc to close",
            centerX, height - 20, (0xFF shl 24) or 0x8A8F9C
        )
    }

    private fun drawSearchBox(graphics: GuiGraphicsExtractor) {
        val left = searchBoxLeft()
        graphics.fill(left, searchBoxTop, left + searchBoxWidth, searchBoxTop + searchBoxHeight, (0xB0 shl 24) or 0x1A1D24)
        graphics.outline(left, searchBoxTop, searchBoxWidth, searchBoxHeight, (0xFF shl 24) or 0x7AA2FF)

        val cursor = if ((System.currentTimeMillis() / 500) % 2 == 0L) "_" else ""
        val text = if (searchQuery.isEmpty()) "Type to search modules..." else searchQuery + cursor
        val color = if (searchQuery.isEmpty()) (0xFF shl 24) or 0x6A6F7C else (0xFF shl 24) or 0xE8EAF0
        graphics.text(font, text, left + 6, searchBoxTop + 4, color, false)
    }

    private fun advanceAnimation() {
        val now = System.nanoTime()
        val dt = ((now - lastFrameNanos) / 1_000_000_000.0).coerceIn(0.0, 0.1)
        lastFrameNanos = now
        val lerp = 1.0 - exp(-12.0 * dt)
        focusIndex += ((targetIndex - focusIndex) * lerp).toFloat()
        if (abs(focusIndex - targetIndex) < 0.001f) focusIndex = targetIndex.toFloat()
    }

    private fun scaleForDistance(distance: Float): Float {
        val steps = abs(distance).coerceAtMost(maxDistance)
        return 1f - steps * scalePerStep
    }

    /**
     * Draws the card at a fixed logical size (cardSize x cardSize) and lets the
     * pose transform shrink the whole thing for distant cards - title and
     * description scale down with the box instead of overflowing it.
     */
    private fun drawCard(graphics: GuiGraphicsExtractor, index: Int, distance: Float, centerX: Int, centerY: Int) {
        val card = cards[index]
        val scale = scaleForDistance(distance)
        val isFront = abs(distance) < 0.5f
        val x = (centerX + distance * slotSpacing).roundToInt()

        val pose = graphics.pose()
        pose.pushMatrix()
        pose.scaleAround(scale, x.toFloat(), centerY.toFloat())

        val left = x - cardSize / 2
        val top = centerY - cardSize / 2
        val alpha = (255 * (0.35f + 0.65f * scale)).roundToInt().coerceIn(0, 255)

        val enabled = card.isEnabled(ConfigManager.config)
        val borderColor = (alpha shl 24) or (if (enabled) 0x7AA2FF else 0x3A3F4B)

        val backgroundColor = (((alpha * 0.85f).roundToInt()) shl 24) or 0x1A1D24
        graphics.fill(left, top, left + cardSize, top + cardSize, backgroundColor)
        graphics.outline(left, top, cardSize, cardSize, borderColor)

        graphics.centeredText(font, card.title, x, top + 10, (alpha shl 24) or 0xE8EAF0)

        if (isFront) {
            val maxTextWidth = cardSize - textMargin * 2

            val descColor = (alpha shl 24) or 0xA0A6B4
            val descLines = font.split(Component.literal(card.description), maxTextWidth)
            var descY = top + cardSize / 2 - 18 - (descLines.size * font.lineHeight) / 2
            for (line in descLines) {
                graphics.centeredText(font, line, x, descY, descColor)
                descY += font.lineHeight
            }

            drawToggle(graphics, x - toggleWidth / 2, top + cardSize - 30, enabled, alpha)

            val hintLines = font.split(Component.literal("Click elsewhere to configure"), maxTextWidth)
            var hintY = top + cardSize - 8 - hintLines.size * font.lineHeight
            for (line in hintLines) {
                graphics.centeredText(font, line, x, hintY, (alpha shl 24) or 0x6A6F7C)
                hintY += font.lineHeight
            }
        }

        pose.popMatrix()
    }

    private fun drawToggle(graphics: GuiGraphicsExtractor, x: Int, y: Int, enabled: Boolean, alpha: Int) {
        val trackColor = (alpha shl 24) or (if (enabled) 0x4C6FCC else 0x2A2D34)
        graphics.fill(x, y, x + toggleWidth, y + toggleHeight, trackColor)
        graphics.outline(x, y, toggleWidth, toggleHeight, (alpha shl 24) or 0x50545F)

        val knobSize = toggleHeight - 4
        val knobX = if (enabled) x + toggleWidth - knobSize - 2 else x + 2
        graphics.fill(knobX, y + 2, knobX + knobSize, y + 2 + knobSize, (alpha shl 24) or 0xF0F2F6)
    }
}
