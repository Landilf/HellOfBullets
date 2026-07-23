package ru.landilf.hellofbullets.domain.model.battle.common.attackpattern

import ru.landilf.hellofbullets.domain.model.battle.common.projectile.RocketHomingConfig
import ru.landilf.hellofbullets.domain.model.common.FloatRange

data class AttackPattern(
    val id: Long,
    val projectileType: ProjectileType,
    val spawnSection: ArenaEdgeSection,
    val targetSections: List<ArenaEdgeSection>,
    val projectileCount: Int,
    val projectileSpeedRange: FloatRange,
    val projectileDamage: Int,
    val projectileHitRadius: Float,
    val projectileLifetimeMs: Int,
    val warningDurationMs: Int = 0,
    val rocketHomingConfig: RocketHomingConfig? = null
) {
    init {
        require(targetSections.isNotEmpty()) {
            "AttackPattern требует хотя бы один элемент в targetSections"
        }
        require(warningDurationMs >= 0) {
            "Длительность предупреждения в AttackPattern не может быть отрицательной"
        }
        if (projectileType == ProjectileType.ROCKET) {
            requireNotNull(rocketHomingConfig) {
                "AttackPattern должен содержать конфигурацию самонаведения ракеты"
            }
        }
    }
}