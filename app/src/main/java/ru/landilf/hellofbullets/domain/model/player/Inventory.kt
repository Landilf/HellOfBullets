package ru.landilf.hellofbullets.domain.model.player

import ru.landilf.hellofbullets.domain.model.equipment.Item

data class Inventory(
    val ownedItems: List<Item>
)