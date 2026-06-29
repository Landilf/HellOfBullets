package ru.landilf.hellofbullets.data.legacy.player

data class PlayerProfile(
    val id: Long,
    val name: String,
    val level: Int,
    val expAmount: Int,
    val silverAmount: Int,
)