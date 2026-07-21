package ru.landilf.hellofbullets.domain.usecase

import ru.landilf.hellofbullets.domain.engine.battle.common.random.BattleRandomSeedGenerator
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.AttackEmitterState
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.AttackWave
import ru.landilf.hellofbullets.domain.model.battle.common.random.BattleRandomState
import ru.landilf.hellofbullets.domain.model.battle.survival.SurvivalGameState
import ru.landilf.hellofbullets.domain.model.battle.survival.SurvivalWavePhase
import ru.landilf.hellofbullets.domain.model.battle.survival.SurvivalWaveState
import ru.landilf.hellofbullets.domain.model.common.GameFieldSize
import ru.landilf.hellofbullets.domain.model.config.survival.DefaultSurvivalPlayerConfig
import ru.landilf.hellofbullets.domain.model.player.PlayerStats
import javax.inject.Inject

class CreateDefaultSurvivalGameStateUseCase @Inject constructor(
    private val getDefaultSurvivalGameConfigUseCase: GetDefaultSurvivalGameConfigUseCase,
    private val buildSurvivalGameStateUseCase: BuildSurvivalGameStateUseCase,
    private val battleRandomSeedGenerator: BattleRandomSeedGenerator
) {
    operator fun invoke(
        fieldSize: GameFieldSize
    ): SurvivalGameState {
        val config = getDefaultSurvivalGameConfigUseCase()
        val playerStats = createInitialPlayerStats(config.playerConfig)
        val initialWaveState = createInitialWaveState(config.waves)
        val randomState = BattleRandomState(
            seed = battleRandomSeedGenerator.nextSeed()
        )

        return buildSurvivalGameStateUseCase(
            playerStats = playerStats,
            playerHitRadius = config.playerConfig.hitRadius,
            initialWaveState = initialWaveState,
            randomState = randomState,
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

    private fun createInitialWaveState(
        waves: List<AttackWave>
    ): SurvivalWaveState {
        require(waves.isNotEmpty()) {
            "SurvivalWaveState требует хотя бы один элемент в waves"
        }

        val firstWave = waves.first()

        return SurvivalWaveState(
            waves = waves,
            currentWaveIndex = 0,
            phase = SurvivalWavePhase.ACTIVE,
            timeUntilPhaseEndMs = firstWave.durationMs,
            emitterStates = firstWave.emitters.map { emitter ->
                AttackEmitterState(
                    timeUntilNextVolleyMs = emitter.initialDelayMs
                )
            }
        )
    }
}