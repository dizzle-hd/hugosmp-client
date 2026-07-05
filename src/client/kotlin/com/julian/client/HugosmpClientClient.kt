package com.julian.client

import com.julian.client.config.ConfigManager
import com.julian.client.feature.AutoReconnectFeature
import com.julian.client.feature.ChatTimestampsFeature
import com.julian.client.feature.DeathCoordsFeature
import com.julian.client.feature.MovementModesFeature
import com.julian.client.feature.WaypointFeature
import com.julian.client.hud.HudFeatureRegistry
import com.julian.client.keybind.HugosmpKeyBindings
import net.fabricmc.api.ClientModInitializer

object HugosmpClientClient : ClientModInitializer {
	override fun onInitializeClient() {
		ConfigManager.load()
		HudFeatureRegistry.registerAll()
		HugosmpKeyBindings.register()
		DeathCoordsFeature.register()
		ChatTimestampsFeature.register()
		MovementModesFeature.register()
		WaypointFeature.register()
		AutoReconnectFeature.register()
	}
}
