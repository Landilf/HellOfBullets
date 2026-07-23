package ru.landilf.hellofbullets.domain.model.battle.common.projectile

import ru.landilf.hellofbullets.domain.model.common.Vector2

data class RocketProjectile(
    override val id: Long,
    override val damage: Int,
    override val hitRadius: Float,
    override val remainingLifetimeMs: Int,
    val position: Vector2,
    val velocity: Vector2,
    val remainingHomingTimeMs: Int,
    val maxTurnRateRadiansPerSecond: Float
) : Projectile() {
    init {
        require(remainingHomingTimeMs >= 0) {
            "Оставшееся время наведения в RocketProjectile должно быть неотрицательным"
        }
        require(
            maxTurnRateRadiansPerSecond.isFinite()
                    && maxTurnRateRadiansPerSecond > 0f
        ) {
            "Угол поворота в RocketProjectile должен быть конечным положительным числом"
        }
    }
}
