package com.example.hellofbullets.domain.model.player

import com.example.hellofbullets.domain.model.equipment.Item

data class Inventory(
    val ownedItems: List<Item>
)