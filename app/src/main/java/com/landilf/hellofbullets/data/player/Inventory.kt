package com.landilf.hellofbullets.data.player

import com.landilf.hellofbullets.data.equipment.Item

data class Inventory(
    val ownedItems: List<Item>
)