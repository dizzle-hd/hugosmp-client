package com.julian.client.feature

import com.julian.client.config.ConfigManager
import com.julian.client.config.Waypoint
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.blockentity.BeaconRenderer

/**
 * Renders every waypoint (manual or the automatic death marker) as a vanilla-
 * style beacon beam with a floating label - no map icon, just a 3D marker you
 * can see from a distance or look towards, same idea as Xaero's waypoints.
 */
object WaypointFeature {
    private const val BEAM_HEIGHT = 320
    private const val LABEL_MAX_DISTANCE = 128.0

    fun register() {
        LevelRenderEvents.COLLECT_SUBMITS.register { context ->
            val config = ConfigManager.config
            if (!config.waypointsEnabled || config.waypoints.isEmpty()) return@register

            val mc = Minecraft.getInstance()
            val player = mc.player ?: return@register
            val currentDimension = player.level().dimension().identifier().path
            val camera = mc.gameRenderer.mainCamera
            val cameraPos = camera.position()

            val visible = config.waypoints
                .filter { it.dimension == currentDimension }
                .map { it to it.distanceSquaredFrom(cameraPos.x, cameraPos.y, cameraPos.z) }
                .sortedByDescending { it.second } // farthest first, so nearer labels end up drawn on top

            val poseStack = context.poseStack()
            val collector = context.submitNodeCollector()
            val bufferSource = context.bufferSource()
            val font = mc.font
            val rotation = camera.rotation()

            for ((waypoint, distanceSquared) in visible) {
                val relX = waypoint.x + 0.5 - cameraPos.x
                val relY = waypoint.y.toDouble() - cameraPos.y
                val relZ = waypoint.z + 0.5 - cameraPos.z

                poseStack.pushPose()
                poseStack.translate(relX, relY, relZ)
                BeaconRenderer.submitBeaconBeam(
                    poseStack, collector, BeaconRenderer.BEAM_LOCATION,
                    1f, 0f, 0, BEAM_HEIGHT, waypoint.colorRGB,
                    BeaconRenderer.SOLID_BEAM_RADIUS, BeaconRenderer.BEAM_GLOW_RADIUS
                )
                poseStack.popPose()

                if (distanceSquared < LABEL_MAX_DISTANCE * LABEL_MAX_DISTANCE) {
                    drawLabel(poseStack, bufferSource, font, waypoint.label, waypoint.colorRGB, relX, relY + 2.2, relZ, rotation)
                }
            }
        }
    }

    private fun Waypoint.distanceSquaredFrom(x: Double, y: Double, z: Double): Double {
        val dx = (this.x + 0.5) - x
        val dy = this.y - y
        val dz = (this.z + 0.5) - z
        return dx * dx + dy * dy + dz * dz
    }

    /**
     * Same billboard trick vanilla uses for entity nametags - fixed world-space
     * scale, so distance alone (via normal 3D perspective) keeps it a sensible
     * size. Styled like the rest of the mod (dark slate backdrop) rather than
     * a plain vanilla nametag, with the text tinted to the waypoint's own
     * beacon color so the label reads as "this marker's" label at a glance.
     */
    private fun drawLabel(
        poseStack: com.mojang.blaze3d.vertex.PoseStack,
        bufferSource: net.minecraft.client.renderer.MultiBufferSource,
        font: Font,
        text: String,
        colorRGB: Int,
        relX: Double, relY: Double, relZ: Double,
        rotation: org.joml.Quaternionf
    ) {
        val textWidth = font.width(text)

        poseStack.pushPose()
        poseStack.translate(relX, relY, relZ)
        poseStack.mulPose(rotation)
        poseStack.scale(-0.025f, -0.025f, 0.025f)

        val textColor = (0xFF shl 24) or (colorRGB and 0xFFFFFF)
        val backgroundColor = (0xB0 shl 24) or 0x1A1D24
        font.drawInBatch(
            text, -textWidth / 2f, 0f, textColor, false,
            poseStack.last().pose(), bufferSource, Font.DisplayMode.SEE_THROUGH, backgroundColor, 0xF000F0
        )
        poseStack.popPose()
    }
}
