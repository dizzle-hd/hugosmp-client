package com.julian.client.mixin

import com.julian.client.mixinext.ItemEntityRenderStateExtension
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Unique

@Mixin(ItemEntityRenderState::class)
abstract class ItemEntityRenderStateMixin : ItemEntityRenderStateExtension {
    @Unique
    override var hugosmpScale: Float = 1f
}
