package ru.landilf.hellofbullets.data.legacy.player

data class PlayerState(
    val playerProfile: PlayerProfile,
    val playerBuild: PlayerBuild,
    val inventory: Inventory
)
