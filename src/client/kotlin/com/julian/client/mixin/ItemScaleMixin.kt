package com.julian.client.mixin

import com.julian.client.mixinext.scaleFactorFor
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

private const val ITEM_METHOD =
    "item(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;III)V"

/**
 * Scales the rendered icon of curated items (hotbar, inventories, tooltips)
 * around its own center. Injects into the single private method all public
 * `item(...)` overloads funnel through, so one mixin covers every call site.
 */
@Mixin(GuiGraphicsExtractor::class)
class ItemScaleMixin {

    @Inject(method = [ITEM_METHOD], at = [At("HEAD")])
    private fun hugosmp_pushItemScale(
        entity: LivingEntity?, level: Level?, stack: ItemStack, x: Int, y: Int, seed: Int, ci: CallbackInfo
    ) {
        val scale = scaleFactorFor(stack)
        if (scale == 1f) return
        val self = (this as Any) as GuiGraphicsExtractor
        self.pose().pushMatrix().scaleAround(scale, x + 8f, y + 8f)
    }

    @Inject(method = [ITEM_METHOD], at = [At("RETURN")])
    private fun hugosmp_popItemScale(
        entity: LivingEntity?, level: Level?, stack: ItemStack, x: Int, y: Int, seed: Int, ci: CallbackInfo
    ) {
        val scale = scaleFactorFor(stack)
        if (scale == 1f) return
        val self = (this as Any) as GuiGraphicsExtractor
        self.pose().popMatrix()
    }
}
