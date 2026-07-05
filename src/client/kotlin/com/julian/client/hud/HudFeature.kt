package com.julian.client.hud

import com.julian.client.config.HugosmpConfig
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphicsExtractor

data class HudPoint(val x: Int, val y: Int)
data class HudSize(val width: Int, val height: Int)

/**
 * Extension point for HUD overlays. Adding a new HUD element means adding one
 * implementation here and registering it in [HudFeatureRegistry] - nothing
 * else in the render plumbing needs to change.
 *
 * Position/size support exists so the [com.julian.client.gui.HudEditorScreen]
 * can show a draggable box for every enabled element without needing to know
 * anything about what that element actually draws.
 */
interface HudFeature {
    val id: String
    val displayName: String

    fun isEnabled(config: HugosmpConfig): Boolean

    fun render(graphics: GuiGraphicsExtractor, deltaTracker: DeltaTracker, config: HugosmpConfig)

    /** Called once per client tick while [isEnabled] is true. Optional - most HUD elements don't need it. */
    fun tick(config: HugosmpConfig) {}

    fun getPosition(config: HugosmpConfig): HudPoint
    fun setPosition(config: HugosmpConfig, point: HudPoint)

    fun getScale(config: HugosmpConfig): Float
    fun setScale(config: HugosmpConfig, scale: Float)

    /** Unscaled footprint at scale 1.0 - the editor multiplies this by [getScale] itself. */
    fun measureSize(config: HugosmpConfig): HudSize
}
