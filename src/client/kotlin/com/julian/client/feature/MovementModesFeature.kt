package com.julian.client.feature

import com.julian.client.config.ConfigManager
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft

/**
 * Two small input QoL modes: auto-sprint while moving forward, and a
 * press-once-to-lock sneak. Both just automate holding a vanilla key -
 * no different from what the player could already do by hand.
 */
object MovementModesFeature {
    private var sneakLocked = false
    private var wasShiftDown = false

    fun register() {
        ClientTickEvents.END_CLIENT_TICK.register {
            val config = ConfigManager.config
            val mc = Minecraft.getInstance()
            val player = mc.player
            if (player == null) {
                sneakLocked = false
                return@register
            }

            if (config.toggleSprintEnabled && mc.options.keyUp.isDown) {
                player.setSprinting(true)
            }

            if (config.toggleSneakEnabled) {
                val shiftDown = mc.options.keyShift.isDown
                if (shiftDown && !wasShiftDown) {
                    sneakLocked = !sneakLocked
                }
                wasShiftDown = shiftDown
                if (sneakLocked) {
                    player.setShiftKeyDown(true)
                }
            } else {
                sneakLocked = false
            }
        }
    }
}
