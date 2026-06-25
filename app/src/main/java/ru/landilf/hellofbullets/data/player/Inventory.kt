package ru.landilf.hellofbullets.data.player

import ru.landilf.hellofbullets.data.equipment.Item

data class Inventory(
    val ownedItems: List<Item>
)