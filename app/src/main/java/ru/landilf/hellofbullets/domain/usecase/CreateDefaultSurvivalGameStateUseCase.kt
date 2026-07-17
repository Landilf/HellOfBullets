package ru.landilf.hellofbullets.domain.usecase

import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.AttackPattern
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.AttackWave
import ru.landilf.hellofbullets.domain.model.battle.survival.SurvivalGameState
import ru.landilf.hellofbullets.domain.model.battle.survival.SurvivalWaveState
import ru.landilf.hellofbullets.domain.model.common.GameFieldSize
import ru.landilf.hellofbullets.domain.model.config.survival.DefaultSurvivalPlayerConfig
import ru.landilf.hellofbullets.domain.model.config.survival.DefaultSurvivalWaveConfig
import ru.landilf.hellofbullets.domain.model.player.PlayerStats
import javax.inject.Inject

class CreateDefaultSurvivalGameStateUseCase @Inject constructor(
    private val getDefaultSurvivalGameConfigUseCase: GetDefaultSurvivalGameConfigUseCase,
    private val buildSurvivalGameStateUseCase: BuildSurvivalGameStateUseCase
) {
    operator fun invoke(
        fieldSize: GameFieldSize
    ): SurvivalGameState {
        val config = getDefaultSurvivalGameConfigUseCase()
        val playerStats = createInitialPlayerStats(config.playerConfig)
        val initialWaveState = createInitialWaveState(config.waveConfig)

        return buildSurvivalGameStateUseCase(
            playerStats = playerStats,
            playerHitRadius = config.playerConfig.hitRadius,
            initialWaveState = initialWaveState,
            fieldSize = fieldSize
        )
    }

    private fun createInitialPlayerStats(playerConfig: DefaultSurvivalPlayerConfig): PlayerStats {
        return PlayerStats(
            maxHp = playerConfig.maxHp,
            damage = playerConfig.damage,
            defense = playerConfig.defense,
            cooldownReduction = playerConfig.cooldownReduction,
            effectDurationBonus = playerConfig.effectDurationBonus
        )
    }

    private fun createInitialWaveState(waveConfig: DefaultSurvivalWaveConfig): SurvivalWaveState {
        val initialPattern = AttackPattern(
            id = waveConfig.patternId,
            projectileType = waveConfig.projectileType,
            spawnZone = waveConfig.spawnZone,
            projectileCount = waveConfig.projectileCount,
            spawnIntervalMs = waveConfig.spawnIntervalMs,
            projectileSpeed = waveConfig.projectileSpeed,
            projectileDamage = waveConfig.projectileDamage,
            projectileHitRadius = waveConfig.projectileHitRadius,
            projectileLifetimeMs = waveConfig.projectileLifetimeMs
        )

        val initialWave = AttackWave(
            id = waveConfig.waveId,
            patterns = listOf(initialPattern),
            durationMs = waveConfig.waveDurationMs,
            breakAfterMs = waveConfig.breakAfterMs
        )

        return SurvivalWaveState(
            currentWave = initialWave,
            currentPatternIndex = 0,
            elapsedWaveTimeMs = 0,
            timeUntilNextVolleyMs = initialPattern.spawnIntervalMs
        )
    }
}