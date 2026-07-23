package ru.landilf.hellofbullets.domain.model.battle.common.projectile

import ru.landilf.hellofbullets.domain.model.common.Vector2

data class LaserProjectile(
    override val id: Long,
    override val damage: Int,
    override val hitRadius: Float,
    override val remainingLifetimeMs: Int,
    val startPosition: Vector2,
    val endPosition: Vector2,
    val phase: LaserPhase,
    val remainingWarningMs: Int
) : Projectile() {
    init {
        require(remainingWarningMs >= 0) {
            "Оставшееся время предупреждения в LaserProjectile не может быть отрицательным"
        }

        when (phase) {
            LaserPhase.WARNING -> require(remainingWarningMs > 0) {
                "LaserProjectile в фазе WARNING должен иметь положительное время предупреждения"
            }

            LaserPhase.ACTIVE -> require(remainingWarningMs == 0) {
                "LaserProjectile в фазе ACTIVE не должен иметь оставшееся время предупреждения"
            }
        }
    }
}
