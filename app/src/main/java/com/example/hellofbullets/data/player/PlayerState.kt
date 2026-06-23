package com.example.hellofbullets.data.player

import com.example.hellofbullets.data.player.Inventory
import com.example.hellofbullets.data.equipment.Item

data class PlayerState(
    val playerProfile: PlayerProfile,
    val playerBuild: PlayerBuild,
    val inventory: Inventory
)
