package com.julian.client.config

import com.google.gson.GsonBuilder
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.LoggerFactory
import java.nio.file.Files

object ConfigManager {
    private val LOGGER = LoggerFactory.getLogger("hugosmp-client/config")
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val path = FabricLoader.getInstance().configDir.resolve("hugosmp-client.json")

    var config: HugosmpConfig = HugosmpConfig()
        private set

    fun load() {
        config = try {
            if (Files.exists(path)) {
                Files.newBufferedReader(path).use { reader ->
                    gson.fromJson(reader, HugosmpConfig::class.java) ?: HugosmpConfig()
                }
            } else {
                HugosmpConfig()
            }
        } catch (e: Exception) {
            LOGGER.warn("Failed to load hugosmp-client.json, falling back to defaults", e)
            HugosmpConfig()
        }
        if (!Files.exists(path)) save()
    }

    fun save() {
        try {
            Files.createDirectories(path.parent)
            Files.newBufferedWriter(path).use { writer -> gson.toJson(config, writer) }
        } catch (e: Exception) {
            LOGGER.warn("Failed to save hugosmp-client.json", e)
        }
    }
}
