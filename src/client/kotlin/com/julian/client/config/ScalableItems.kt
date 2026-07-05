package com.julian.client.config

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items

/** One entry in the curated "make this bigger" item picker. */
data class ScalableItem(val item: Item, val displayName: String) {
    val id: String get() = BuiltInRegistries.ITEM.getKey(item).toString()
}

/** Curated set of valuable/PvP-relevant items eligible for the Item Scale feature - not every item in the game. */
object ScalableItems {
    val all: List<ScalableItem> = listOf(
        ScalableItem(Items.DIAMOND, "Diamond"),
        ScalableItem(Items.EMERALD, "Emerald"),
        ScalableItem(Items.NETHERITE_INGOT, "Netherite Ingot"),
        ScalableItem(Items.NETHERITE_SWORD, "Netherite Sword"),
        ScalableItem(Items.TOTEM_OF_UNDYING, "Totem of Undying"),
        ScalableItem(Items.ENCHANTED_GOLDEN_APPLE, "Enchanted Golden Apple"),
        ScalableItem(Items.GOLDEN_APPLE, "Golden Apple"),
        ScalableItem(Items.ELYTRA, "Elytra"),
        ScalableItem(Items.ENDER_PEARL, "Ender Pearl"),
        ScalableItem(Items.SHULKER_BOX, "Shulker Box")
    )
}
