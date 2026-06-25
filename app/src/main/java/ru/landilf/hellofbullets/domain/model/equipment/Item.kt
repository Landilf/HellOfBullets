package ru.landilf.hellofbullets.domain.model.equipment

sealed class Item {
    abstract val id: Long
    abstract val name: String
    abstract val level: Int
    abstract val maxLevel: Int
}