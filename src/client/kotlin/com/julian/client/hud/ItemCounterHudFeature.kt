package com.julian.client.hud

import com.julian.client.config.HugosmpConfig
import com.julian.client.config.ScalableItems
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.world.entity.EquipmentSlot

/** Shows how many of one chosen item (e.g. Totems) you're carrying - picked from the same curated list as Item Scale. */
object ItemCounterHudFeature : HudFeature {
    override val id: String = "item-counter"
    override val displayName: String = "Item Counter"
    override fun isEnabled(config: HugosmpConfig): Boolean = config.itemCounterHudEnabled

    override fun getPosition(config: HugosmpConfig): HudPoint = HudPoint(config.itemCounterHudX, config.itemCounterHudY)
    override fun setPosition(config: HugosmpConfig, point: HudPoint) {
        config.itemCounterHudX = point.x
        config.itemCounterHudY = point.y
    }

    override fun getScale(config: HugosmpConfig): Float = config.itemCounterHudScale
    override fun setScale(config: HugosmpConfig, scale: Float) {
        config.itemCounterHudScale = scale
    }

    override fun measureSize(config: HugosmpConfig): HudSize = HudTextBox.measure("Netherite Ingot: 99")

    override fun render(graphics: GuiGraphicsExtractor, deltaTracker: DeltaTracker, config: HugosmpConfig) {
        val player = Minecraft.getInstance().player ?: return
        val scalable = ScalableItems.all.find { it.id == config.itemCounterItemId } ?: return

        var count = 0
        for (stack in player.inventory.nonEquipmentItems) {
            if (stack.item === scalable.item) count += stack.count
        }
        val offhand = player.getItemBySlot(EquipmentSlot.OFFHAND)
        if (offhand.item === scalable.item) count += offhand.count

        HudTextBox.draw(graphics, "${scalable.displayName}: $count", config.itemCounterHudX, config.itemCounterHudY)
    }
}
