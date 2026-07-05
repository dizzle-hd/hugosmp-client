package com.julian.client.mixin

import com.julian.client.mixinext.ItemEntityRenderStateExtension
import com.julian.client.mixinext.scaleFactorFor
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.ItemEntityRenderer
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.world.entity.item.ItemEntity
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

private const val EXTRACT_METHOD =
    "extractRenderState(Lnet/minecraft/world/entity/item/ItemEntity;Lnet/minecraft/client/renderer/entity/state/ItemEntityRenderState;F)V"
private const val SUBMIT_METHOD =
    "submit(Lnet/minecraft/client/renderer/entity/state/ItemEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V"

/** Scales curated items when they're lying on the ground, so a valuable loot pile is easier to spot from afar. */
@Mixin(ItemEntityRenderer::class)
class ItemEntityScaleMixin {

    @Inject(method = [EXTRACT_METHOD], at = [At("TAIL")])
    private fun hugosmp_captureScale(entity: ItemEntity, state: ItemEntityRenderState, partialTick: Float, ci: CallbackInfo) {
        (state as ItemEntityRenderStateExtension).hugosmpScale = scaleFactorFor(entity.item)
    }

    @Inject(method = [SUBMIT_METHOD], at = [At("HEAD")])
    private fun hugosmp_pushItemEntityScale(
        state: ItemEntityRenderState, poseStack: PoseStack, collector: SubmitNodeCollector,
        cameraState: CameraRenderState, ci: CallbackInfo
    ) {
        val scale = (state as ItemEntityRenderStateExtension).hugosmpScale
        if (scale == 1f) return
        poseStack.pushPose()
        poseStack.scale(scale, scale, scale)
    }

    @Inject(method = [SUBMIT_METHOD], at = [At("RETURN")])
    private fun hugosmp_popItemEntityScale(
        state: ItemEntityRenderState, poseStack: PoseStack, collector: SubmitNodeCollector,
        cameraState: CameraRenderState, ci: CallbackInfo
    ) {
        val scale = (state as ItemEntityRenderStateExtension).hugosmpScale
        if (scale == 1f) return
        poseStack.popPose()
    }
}
