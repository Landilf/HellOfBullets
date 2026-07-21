package ru.landilf.hellofbullets.domain.model.battle.common.attackpattern

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
    val projectileLifetimeMs: Int
) {
    init {
        require(targetSections.isNotEmpty()) {
            "AttackPattern требует хотя бы один элемент в targetSections"
        }
    }
}