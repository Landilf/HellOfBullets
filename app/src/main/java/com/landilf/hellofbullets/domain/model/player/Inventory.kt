package com.landilf.hellofbullets.domain.model.player

import com.landilf.hellofbullets.domain.model.equipment.Item

data class Inventory(
    val ownedItems: List<Item>
)