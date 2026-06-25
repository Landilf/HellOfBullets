package com.landilf.hellofbullets.data.equipment

sealed class Item {
    abstract val id: Long
    abstract val name: String
    abstract val level: Int
    abstract val maxLevel: Int
}