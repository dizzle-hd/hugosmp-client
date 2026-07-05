package com.julian.client.mixinext

import com.julian.client.config.ConfigManager
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.ItemStack

/** Shared by the GUI-icon and ground-item mixins to decide whether/how much to scale a given stack. */
fun scaleFactorFor(stack: ItemStack): Float {
    if (stack.isEmpty) return 1f
    val config = ConfigManager.config
    val id = BuiltInRegistries.ITEM.getKey(stack.item).toString()
    return if (id in config.scaledItemIds) config.itemScaleAmount else 1f
}
