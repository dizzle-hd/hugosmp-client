package com.julian.client.mixin

import com.julian.client.config.ConfigManager
import net.minecraft.client.renderer.LightmapRenderStateExtractor
import net.minecraft.client.renderer.state.LightmapRenderState
import org.joml.Vector3f
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

/**
 * Forces every light level to render fully lit when Fullbright is enabled -
 * not just nudging the gamma curve like the vanilla brightness slider, but
 * overriding sky/block/ambient contribution directly so caves and nights
 * are genuinely as bright as broad daylight.
 */
@Mixin(LightmapRenderStateExtractor::class)
class FullbrightMixin {

    @Inject(method = ["extract"], at = [At("TAIL")])
    private fun hugosmp_applyFullbright(state: LightmapRenderState, partialTick: Float, ci: CallbackInfo) {
        if (!ConfigManager.config.fullbrightEnabled) return
        val white = Vector3f(1f, 1f, 1f)
        state.brightness = 1f
        state.blockFactor = 1f
        state.skyFactor = 1f
        state.ambientColor = white
        state.blockLightTint = white
        state.skyLightColor = white
        state.darknessEffectScale = 0f
        state.bossOverlayWorldDarkening = 0f
    }
}
