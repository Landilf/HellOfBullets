package ru.landilf.hellofbullets.domain.model.player

data class PlayerState(
    val playerProfile: PlayerProfile,
    val playerBuild: PlayerBuild,
    val inventory: Inventory
)
