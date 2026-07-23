package ru.landilf.hellofbullets.domain.model.battle.common.projectile

data class RocketHomingConfig(
    val durationMs: Int,
    val maxTurnRateRadiansPerSecond: Float
) {
    init {
        require(durationMs > 0) {
            "Длительность самонаведения в RocketHomingConfig должна быть больше нуля"
        }
        require(maxTurnRateRadiansPerSecond > 0f) {
            "Максимальная скорость поворота в RocketHomingConfig должна быть больше нуля"
        }
    }
}