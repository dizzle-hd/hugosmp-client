package com.julian.client.feature

import com.julian.client.config.ConfigManager
import com.julian.client.config.Waypoint
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component

private const val DEATH_WAYPOINT_LABEL = "Death"
private const val DEATH_WAYPOINT_COLOR = 0xFF5C5C

/** Sends a client-only chat message and drops a beacon waypoint at the spot you died, so you can find your way back. */
object DeathCoordsFeature {
    private var wasAlive = true

    fun register() {
        ClientTickEvents.END_CLIENT_TICK.register {
            val config = ConfigManager.config
            val mc = Minecraft.getInstance()
            val player = mc.player

            if (!config.deathCoordsEnabled || player == null) {
                wasAlive = true
                return@register
            }

            val isDead = player.isDeadOrDying
            if (isDead && wasAlive) {
                val pos = player.blockPosition()
                val dimension = player.level().dimension().identifier().path
                val suffix = if (config.deathCoordsIncludeDimension) " in $dimension" else ""
                mc.gui.chat.addClientSystemMessage(
                    Component.literal("[HugoSMP] You died at ${pos.x}, ${pos.y}, ${pos.z}$suffix")
                )

                config.waypoints.removeIf { it.label == DEATH_WAYPOINT_LABEL }
                config.waypoints.add(Waypoint(pos.x, pos.y, pos.z, DEATH_WAYPOINT_LABEL, DEATH_WAYPOINT_COLOR, dimension))
                config.waypointsEnabled = true
                ConfigManager.save()
            }
            wasAlive = !isDead
        }
    }
}
