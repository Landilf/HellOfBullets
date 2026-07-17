package ru.landilf.hellofbullets.domain.usecase

import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.ProjectileType
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.SpawnZone
import ru.landilf.hellofbullets.domain.model.config.survival.DefaultSurvivalGameConfig
import ru.landilf.hellofbullets.domain.model.config.survival.DefaultSurvivalPlayerConfig
import ru.landilf.hellofbullets.domain.model.config.survival.DefaultSurvivalWaveConfig
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
            waveConfig = DefaultSurvivalWaveConfig(
                waveId = 1L,
                patternId = 1L,
                projectileType = ProjectileType.BULLET,
                spawnZone = SpawnZone.TOP,
                projectileCount = 4,
                spawnIntervalMs = 500,
                projectileSpeed = 75f,
                projectileDamage = 1,
                projectileHitRadius = 2f,
                projectileLifetimeMs = 5000,
                waveDurationMs = 30_000,
                breakAfterMs = 2_000
            )
        )
    }
}