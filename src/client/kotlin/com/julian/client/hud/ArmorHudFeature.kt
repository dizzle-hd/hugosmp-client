package com.julian.client.hud

import com.julian.client.config.HugosmpConfig
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.ItemStack

/** Shows each equipped armor piece with a small durability bar underneath - green/yellow/red as it wears down. */
object ArmorHudFeature : HudFeature {
    override val id: String = "armor"
    override val displayName: String = "Armor HUD"
    override fun isEnabled(config: HugosmpConfig): Boolean = config.armorHudEnabled

    private const val ICON_SIZE = 16
    private const val BAR_HEIGHT = 2
    private const val GAP = 4
    private const val STEP = ICON_SIZE + GAP
    private val slots = listOf(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)

    override fun getPosition(config: HugosmpConfig): HudPoint = HudPoint(config.armorHudX, config.armorHudY)
    override fun setPosition(config: HugosmpConfig, point: HudPoint) {
        config.armorHudX = point.x
        config.armorHudY = point.y
    }

    override fun getScale(config: HugosmpConfig): Float = config.armorHudScale
    override fun setScale(config: HugosmpConfig, scale: Float) {
        config.armorHudScale = scale
    }

    override fun measureSize(config: HugosmpConfig): HudSize =
        HudSize(slots.size * STEP - GAP, ICON_SIZE + BAR_HEIGHT + 2)

    override fun render(graphics: GuiGraphicsExtractor, deltaTracker: DeltaTracker, config: HugosmpConfig) {
        val player = Minecraft.getInstance().player ?: return
        val originX = config.armorHudX
        val originY = config.armorHudY

        for ((index, slot) in slots.withIndex()) {
            val x = originX + index * STEP
            val stack = player.getItemBySlot(slot)
            drawSlot(graphics, x, originY, stack)
        }
    }

    private fun drawSlot(graphics: GuiGraphicsExtractor, x: Int, y: Int, stack: ItemStack) {
        graphics.fill(x, y, x + ICON_SIZE, y + ICON_SIZE, (0x90 shl 24) or 0x1A1D24)
        graphics.outline(x, y, ICON_SIZE, ICON_SIZE, (0x90 shl 24) or 0x3A3F4B)
        if (stack.isEmpty) return

        graphics.item(stack, x, y)

        if (!stack.isDamageableItem) return
        val fraction = 1f - stack.damageValue.toFloat() / stack.maxDamage.toFloat()
        val barColor = when {
            fraction > 0.5f -> (0xFF shl 24) or 0x7CFC98
            fraction > 0.2f -> (0xFF shl 24) or 0xFFD479
            else -> (0xFF shl 24) or 0xFF5C5C
        }
        val barY = y + ICON_SIZE + 1
        graphics.fill(x, barY, x + ICON_SIZE, barY + BAR_HEIGHT, (0xFF shl 24) or 0x2A2D34)
        val filledWidth = (ICON_SIZE * fraction).toInt().coerceAtLeast(1)
        graphics.fill(x, barY, x + filledWidth, barY + BAR_HEIGHT, barColor)
    }
}
