package ru.landilf.hellofbullets.data.legacy.player

import ru.landilf.hellofbullets.data.legacy.equipment.Item

data class Inventory(
    val ownedItems: List<Item>
)