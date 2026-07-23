package ru.landilf.hellofbullets.domain.usecase

import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.AttackPattern
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.AttackWave
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.ProjectileType
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.ArenaEdgeSection
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.AttackEmitter
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
                    emitters = listOf(
                        AttackEmitter(
                            patternOptions = listOf(
                                createBulletPattern(
                                    id = 1L,
                                    spawnSection = ArenaEdgeSection.TOP,
                                    targetSections = listOf(ArenaEdgeSection.BOTTOM),
                                    projectileCount = 3,
                                    speedRange = FloatRange(
                                        min = 45f,
                                        max = 65f
                                    )
                                ),
                                createBulletPattern(
                                    id = 2L,
                                    spawnSection = ArenaEdgeSection.BOTTOM,
                                    targetSections = listOf(ArenaEdgeSection.TOP),
                                    projectileCount = 3,
                                    speedRange = FloatRange(
                                        min = 45f,
                                        max = 65f
                                    )
                                )
                            ),
                            volleyIntervalMs = 800
                        )
                    ),
                    durationMs = 15_000,
                    breakDurationMs = 2_000
                ),
                AttackWave(
                    id = 2L,
                    emitters = listOf(
                        AttackEmitter(
                            patternOptions = listOf(
                                createBulletPattern(
                                    id = 3L,
                                    spawnSection = ArenaEdgeSection.TOP,
                                    targetSections = listOf(
                                        ArenaEdgeSection.BOTTOM,
                                        ArenaEdgeSection.LEFT_LOWER,
                                        ArenaEdgeSection.RIGHT_LOWER
                                    ),
                                    projectileCount = 4,
                                    speedRange = FloatRange(
                                        min = 65f,
                                        max = 90f
                                    )
                                )
                            ),
                            volleyIntervalMs = 650
                        )

                    ),
                    durationMs = 20_000,
                    breakDurationMs = 1_500
                ),
//                AttackWave(
//                    id = 3L,
//                    emitters = listOf(
//                        AttackEmitter(
//                            patternOptions = listOf(
//                                createBulletPattern(
//                                    id = 4L,
//                                    spawnSection = ArenaEdgeSection.LEFT_UPPER,
//                                    targetSections = listOf(
//                                        ArenaEdgeSection.BOTTOM,
//                                        ArenaEdgeSection.RIGHT_LOWER
//                                    ),
//                                    projectileCount = 2,
//                                    speedRange = FloatRange(
//                                        min = 50f,
//                                        max = 75f
//                                    )
//                                ),
//                                createBulletPattern(
//                                    id = 5L,
//                                    spawnSection = ArenaEdgeSection.RIGHT_UPPER,
//                                    targetSections = listOf(
//                                        ArenaEdgeSection.BOTTOM,
//                                        ArenaEdgeSection.LEFT_LOWER
//                                    ),
//                                    projectileCount = 2,
//                                    speedRange = FloatRange(
//                                        min = 50f,
//                                        max = 75f
//                                    )
//                                )
//                            ),
//                            volleyIntervalMs = 750
//                        ),
//                        AttackEmitter(
//                            patternOptions = listOf(
//                                createBulletPattern(
//                                    id = 6L,
//                                    spawnSection = ArenaEdgeSection.TOP,
//                                    targetSections = listOf(
//                                        ArenaEdgeSection.BOTTOM
//                                    ),
//                                    projectileCount = 3,
//                                    speedRange = FloatRange(
//                                        min = 50f,
//                                        max = 75f
//                                    )
//                                ),
//                                createBulletPattern(
//                                    id = 7L,
//                                    spawnSection = ArenaEdgeSection.BOTTOM,
//                                    targetSections = listOf(
//                                        ArenaEdgeSection.TOP
//                                    ),
//                                    projectileCount = 3,
//                                    speedRange = FloatRange(
//                                        min = 50f,
//                                        max = 75f
//                                    )
//                                )
//                            ),
//                            volleyIntervalMs = 500
//                        )
//                    ),
//                    durationMs = 25_000,
//                    breakDurationMs = 1_000
//                ),
                AttackWave(
                    id = 4L,
                    emitters = listOf(
                        AttackEmitter(
                            patternOptions = listOf(
                                createLaserPattern(
                                    id = 8L,
                                    spawnSection = ArenaEdgeSection.TOP,
                                    targetSections = listOf(ArenaEdgeSection.BOTTOM),
                                    projectileCount = 7,
                                    warningDurationMs = 1_000,
                                    projectileLifetimeMs = 750
                                ),
                                createLaserPattern(
                                    id = 9L,
                                    spawnSection = ArenaEdgeSection.LEFT_UPPER,
                                    targetSections = listOf(
                                        ArenaEdgeSection.TOP,
                                        ArenaEdgeSection.RIGHT_LOWER,
                                        ArenaEdgeSection.BOTTOM
                                    ),
                                    projectileCount = 3,
                                    warningDurationMs = 1_000,
                                    projectileLifetimeMs = 750
                                ),
                                createLaserPattern(
                                    id = 10L,
                                    spawnSection = ArenaEdgeSection.RIGHT_UPPER,
                                    targetSections = listOf(
                                        ArenaEdgeSection.TOP,
                                        ArenaEdgeSection.LEFT_LOWER,
                                        ArenaEdgeSection.BOTTOM
                                    ),
                                    projectileCount = 3,
                                    warningDurationMs = 1_000,
                                    projectileLifetimeMs = 750
                                )
                            ),
                            volleyIntervalMs = 800
                        )
                    ),
                    durationMs = 15_000,
                    breakDurationMs = 2_000
                )
            )
        )
    }

    private fun createBulletPattern(
        id: Long,
        spawnSection: ArenaEdgeSection,
        targetSections: List<ArenaEdgeSection>,
        projectileCount: Int,
        speedRange: FloatRange
    ): AttackPattern {
        return AttackPattern(
            id = id,
            projectileType = ProjectileType.BULLET,
            spawnSection = spawnSection,
            targetSections = targetSections,
            projectileCount = projectileCount,
            projectileSpeedRange = speedRange,
            projectileDamage = 1,
            projectileHitRadius = 2f,
            projectileLifetimeMs = 5_000
        )
    }

    private fun createLaserPattern(
        id: Long,
        spawnSection: ArenaEdgeSection,
        targetSections: List<ArenaEdgeSection>,
        projectileCount: Int,
        projectileLifetimeMs: Int,
        warningDurationMs: Int
    ): AttackPattern {
        return AttackPattern(
            id = id,
            projectileType = ProjectileType.LASER,
            spawnSection = spawnSection,
            targetSections = targetSections,
            projectileCount = projectileCount,
            projectileSpeedRange = FloatRange(
                min = 0f,
                max = 0f
            ),
            projectileDamage = 1,
            projectileHitRadius = 1.5f,
            projectileLifetimeMs = projectileLifetimeMs,
            warningDurationMs = warningDurationMs
        )
    }
}
