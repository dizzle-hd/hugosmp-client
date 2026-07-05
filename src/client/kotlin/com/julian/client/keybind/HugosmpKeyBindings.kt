package com.julian.client.keybind

import com.julian.HugosmpClient
import com.julian.client.config.ConfigManager
import com.julian.client.config.Waypoint
import com.julian.client.gui.HudEditorScreen
import com.julian.client.gui.HugosmpConfigScreen
import com.mojang.blaze3d.platform.InputConstants
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
import net.minecraft.client.KeyMapping
import org.lwjgl.glfw.GLFW

object HugosmpKeyBindings {
    private val category = KeyMapping.Category.register(HugosmpClient.id("main"))
    private val waypointColors = listOf(0x7AA2FF, 0x7CFC98, 0xFFD479, 0xFF7AA2, 0xC17AFF)

    val toggleCoordsHud: KeyMapping = KeyMappingHelper.registerKeyMapping(
        KeyMapping("key.hugosmp-client.toggle_coords_hud", InputConstants.UNKNOWN.value, category)
    )

    val openSettings: KeyMapping = KeyMappingHelper.registerKeyMapping(
        KeyMapping("key.hugosmp-client.open_settings", GLFW.GLFW_KEY_RIGHT_SHIFT, category)
    )

    val openHudEditor: KeyMapping = KeyMappingHelper.registerKeyMapping(
        KeyMapping("key.hugosmp-client.open_hud_editor", GLFW.GLFW_KEY_RIGHT_CONTROL, category)
    )

    val setWaypoint: KeyMapping = KeyMappingHelper.registerKeyMapping(
        KeyMapping("key.hugosmp-client.set_waypoint", InputConstants.UNKNOWN.value, category)
    )

    fun register() {
        ClientTickEvents.END_CLIENT_TICK.register { mc ->
            while (toggleCoordsHud.consumeClick()) {
                val config = ConfigManager.config
                config.coordsHudEnabled = !config.coordsHudEnabled
                ConfigManager.save()
            }
            while (openSettings.consumeClick()) {
                mc.setScreen(HugosmpConfigScreen(mc.screen))
            }
            while (openHudEditor.consumeClick()) {
                mc.setScreen(HudEditorScreen(mc.screen))
            }
            while (setWaypoint.consumeClick()) {
                val player = mc.player ?: continue
                val config = ConfigManager.config
                val pos = player.blockPosition()
                val index = config.waypoints.count { it.label.startsWith("Waypoint ") } + 1
                config.waypoints.add(
                    Waypoint(
                        pos.x, pos.y, pos.z, "Waypoint $index",
                        waypointColors[(index - 1) % waypointColors.size],
                        player.level().dimension().identifier().path
                    )
                )
                config.waypointsEnabled = true
                ConfigManager.save()
                mc.gui.chat.addClientSystemMessage(
                    net.minecraft.network.chat.Component.literal("[HugoSMP] Waypoint $index set")
                )
            }
        }
    }
}
