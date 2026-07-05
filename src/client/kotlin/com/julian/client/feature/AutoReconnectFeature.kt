package com.julian.client.feature

import com.julian.client.config.ConfigManager
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.ConnectScreen
import net.minecraft.client.gui.screens.DisconnectedScreen
import net.minecraft.client.multiplayer.ServerData
import net.minecraft.client.multiplayer.TransferState
import net.minecraft.client.multiplayer.resolver.ServerAddress

/**
 * Reconnects to the last server after an involuntary disconnect (kick, crash,
 * lost connection) - never after a manual "Disconnect"/"Save & Quit", which is
 * why the check waits for the real [DisconnectedScreen] rather than firing on
 * every connection close.
 *
 * The AFK module builds on the same mechanism: after such a reconnect, it also
 * waits for the resource pack to finish loading and re-sends `/afk`, so an
 * AFK-lobby kick on HugoSMP (or similarly set up servers, e.g. Donut SMP)
 * doesn't cost you the reward streak.
 */
object AutoReconnectFeature {
    private var lastServer: ServerData? = null
    private var pendingDisconnectCheck = false
    private var reconnectCountdownTicks = -1
    private var awaitingAfkResend = false
    private var afkCountdownTicks = -1

    fun register() {
        ClientPlayConnectionEvents.JOIN.register { _, _, mc ->
            lastServer = mc.currentServer
            if (awaitingAfkResend) {
                awaitingAfkResend = false
                afkCountdownTicks = (ConfigManager.config.afkResendDelaySeconds * 20).toInt().coerceAtLeast(1)
            }
        }
        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            pendingDisconnectCheck = true
        }

        ClientTickEvents.END_CLIENT_TICK.register { mc ->
            if (pendingDisconnectCheck) {
                pendingDisconnectCheck = false
                val config = ConfigManager.config
                val server = lastServer
                if ((config.autoReconnectEnabled || config.afkModeEnabled) && server != null && mc.screen is DisconnectedScreen) {
                    reconnectCountdownTicks = (config.autoReconnectDelaySeconds * 20).toInt().coerceAtLeast(1)
                }
            }

            if (reconnectCountdownTicks > 0) {
                reconnectCountdownTicks--
                if (reconnectCountdownTicks == 0) {
                    reconnect(mc)
                }
            }

            if (afkCountdownTicks > 0) {
                afkCountdownTicks--
                if (afkCountdownTicks == 0) {
                    mc.connection?.sendCommand("afk")
                }
            }
        }
    }

    private fun reconnect(mc: Minecraft) {
        val server = lastServer ?: return
        val parentScreen = mc.screen ?: return
        val address = ServerAddress.parseString(server.ip)
        if (ConfigManager.config.afkModeEnabled) {
            awaitingAfkResend = true
        }
        ConnectScreen.startConnecting(parentScreen, mc, address, server, false, TransferState(emptyMap(), emptyMap(), false))
    }
}
