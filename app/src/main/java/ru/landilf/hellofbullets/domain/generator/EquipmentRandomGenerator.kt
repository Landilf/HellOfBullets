package ru.landilf.hellofbullets.domain.generator

interface EquipmentRandomGenerator {
    fun nextFloat(
        from: Float,
        until: Float
    ): Float

    fun nextInt(until: Int): Int
}