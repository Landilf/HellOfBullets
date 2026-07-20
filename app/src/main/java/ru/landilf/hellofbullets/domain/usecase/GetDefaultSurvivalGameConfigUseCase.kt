package ru.landilf.hellofbullets.domain.usecase

import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.AttackPattern
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.AttackWave
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.ProjectileType
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.ArenaEdgeSection
import ru.landilf.hellofbullets.domain.model.common.FloatRange
import ru.landilf.hellofbullets.domain.model.config.survival.DefaultSurvivalGameConfig
import ru.landilf.hellofbullets.domain.model.config.survival.DefaultSurvivalPlayerConfig
import javax.inject.Inject

class GetDefaultSurvivalGameConfigUseCase @Inject constructor() {
    operator fun invoke(): DefaultSurvivalGameConfig {
        return DefaultSurvivalGameConfig(
            playerConfig = DefaultSurvivalPlayerConfig(
                maxHp = 1,
                damage = 0,
                defense = 0,
                cooldownReduction = 0,
                effectDurationBonus = 0,
                hitRadius = 3f
            ),
            waves = listOf(
                AttackWave(
                    id = 1L,
                    patterns = listOf(
                        createBulletPattern(
                            id = 1L,
                            spawnSection = ArenaEdgeSection.TOP,
                            targetSections = listOf(ArenaEdgeSection.BOTTOM),
                            projectileCount = 3,
                            spawnIntervalMs = 800,
                            speedRange = FloatRange(
                                min = 45f,
                                max = 65f
                            )
                        )
                    ),
                    durationMs = 15_000,
                    breakDurationMs = 2_000
                ),
                AttackWave(
                    id = 2L,
                    patterns = listOf(
                        createBulletPattern(
                            id = 2L,
                            spawnSection = ArenaEdgeSection.TOP,
                            targetSections = listOf(
                                ArenaEdgeSection.BOTTOM,
                                ArenaEdgeSection.LEFT_LOWER,
                                ArenaEdgeSection.RIGHT_LOWER
                            ),
                            projectileCount = 4,
                            spawnIntervalMs = 650,
                            speedRange = FloatRange(
                                min = 65f,
                                max = 90f
                            )
                        )
                    ),
                    durationMs = 20_000,
                    breakDurationMs = 1_500
                ),
                AttackWave(
                    id = 3L,
                    patterns = listOf(
                        createBulletPattern(
                            id = 3L,
                            spawnSection = ArenaEdgeSection.LEFT_UPPER,
                            targetSections = listOf(
                                ArenaEdgeSection.BOTTOM,
                                ArenaEdgeSection.RIGHT_LOWER
                            ),
                            projectileCount = 3,
                            spawnIntervalMs = 500,
                            speedRange = FloatRange(
                                min = 75f,
                                max = 105f
                            )
                        ),
                        createBulletPattern(
                            id = 4L,
                            spawnSection = ArenaEdgeSection.RIGHT_UPPER,
                            targetSections = listOf(
                                ArenaEdgeSection.BOTTOM,
                                ArenaEdgeSection.LEFT_LOWER
                            ),
                            projectileCount = 3,
                            spawnIntervalMs = 500,
                            speedRange = FloatRange(
                                min = 75f,
                                max = 105f
                            )
                        ),
                        createBulletPattern(
                            id = 5L,
                            spawnSection = ArenaEdgeSection.BOTTOM,
                            targetSections = listOf(
                                ArenaEdgeSection.TOP
                            ),
                            projectileCount = 3,
                            spawnIntervalMs = 500,
                            speedRange = FloatRange(
                                min = 75f,
                                max = 105f
                            )
                        )
                    ),
                    durationMs = 25_000,
                    breakDurationMs = 1_000
                )
            )
        )
    }

    private fun createBulletPattern(
        id: Long,
        spawnSection: ArenaEdgeSection,
        targetSections: List<ArenaEdgeSection>,
        projectileCount: Int,
        spawnIntervalMs: Int,
        speedRange: FloatRange
    ): AttackPattern {
        return AttackPattern(
            id = id,
            projectileType = ProjectileType.BULLET,
            spawnSection = spawnSection,
            targetSections = targetSections,
            projectileCount = projectileCount,
            spawnIntervalMs = spawnIntervalMs,
            projectileSpeedRange = speedRange,
            projectileDamage = 1,
            projectileHitRadius = 2f,
            projectileLifetimeMs = 5_000
        )
    }
}
