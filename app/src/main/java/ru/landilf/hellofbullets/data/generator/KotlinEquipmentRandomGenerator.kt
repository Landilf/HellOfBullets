package ru.landilf.hellofbullets.data.generator

import ru.landilf.hellofbullets.domain.generator.EquipmentRandomGenerator
import javax.inject.Inject
import kotlin.random.Random

class KotlinEquipmentRandomGenerator @Inject constructor() : EquipmentRandomGenerator {
    override fun nextFloat(
        from: Float,
        until: Float
    ): Float {
        require(from < until) {
            "Нижняя граница случайного диапазона должна быть меньше верхней"
        }

        return from + (until - from) * Random.nextFloat()
    }

    override fun nextInt(until: Int): Int {
        return Random.nextInt(until)
    }
}