package com.julian.client.feature

import com.julian.client.config.ConfigManager
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.network.chat.Component
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object ChatTimestampsFeature {
    private val formatter24 = DateTimeFormatter.ofPattern("HH:mm")
    private val formatter12 = DateTimeFormatter.ofPattern("hh:mm a")

    fun register() {
        ClientReceiveMessageEvents.MODIFY_GAME.register { message, overlay ->
            val config = ConfigManager.config
            if (overlay || !config.chatTimestampsEnabled) {
                message
            } else {
                val formatter = if (config.chatTimestamps24Hour) formatter24 else formatter12
                Component.literal("[${LocalTime.now().format(formatter)}] ").append(message)
            }
        }
    }
}
