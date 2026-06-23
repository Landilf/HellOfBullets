package com.example.hellofbullets.data.player

data class PlayerProfile(
    val id: Long,
    val name: String,
    val level: Int,
    val expAmount: Int,
    val silverAmount: Int,
)