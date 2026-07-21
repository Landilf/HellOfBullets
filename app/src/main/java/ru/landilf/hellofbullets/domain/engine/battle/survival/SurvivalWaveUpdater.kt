package ru.landilf.hellofbullets.domain.engine.battle.survival

import ru.landilf.hellofbullets.domain.engine.battle.common.ProjectileFactory
import ru.landilf.hellofbullets.domain.engine.battle.common.random.BattleRandomGenerator
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.AttackEmitterState
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.Projectile
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.ProjectileGenerationState
import ru.landilf.hellofbullets.domain.model.battle.common.random.BattleRandomState
import ru.landilf.hellofbullets.domain.model.battle.common.random.RandomResult
import ru.landilf.hellofbullets.domain.model.battle.survival.SurvivalWavePhase
import ru.landilf.hellofbullets.domain.model.battle.survival.SurvivalWaveState
import ru.landilf.hellofbullets.domain.model.battle.survival.SurvivalWaveUpdateResult
import ru.landilf.hellofbullets.domain.model.common.GameFieldSize
import javax.inject.Inject

class SurvivalWaveUpdater @Inject constructor(
    private val projectileFactory: ProjectileFactory,
    private val randomGenerator: BattleRandomGenerator
) {
    fun update(
        waveState: SurvivalWaveState?,
        deltaTimeMs: Int,
        initialGenerationState: ProjectileGenerationState,
        fieldSize: GameFieldSize
    ): SurvivalWaveUpdateResult {
        val initialWaveState = waveState ?: return SurvivalWaveUpdateResult(
            waveState = null,
            spawnedProjectiles = emptyList(),
            nextGenerationState = initialGenerationState
        )

        if (deltaTimeMs <= 0) {
            return SurvivalWaveUpdateResult(
                waveState = initialWaveState,
                spawnedProjectiles = emptyList(),
                nextGenerationState = initialGenerationState
            )
        }

        var updatedWaveState = initialWaveState
        var generationState = initialGenerationState
        var remainingDeltaTimeMs = deltaTimeMs
        val spawnedProjectiles = mutableListOf<Projectile>()

        while (remainingDeltaTimeMs > 0) {
            val updateResult = when (updatedWaveState.phase) {
                SurvivalWavePhase.ACTIVE -> updateActivePhase(
                    waveState = updatedWaveState,
                    deltaTimeMs = remainingDeltaTimeMs,
                    generationState = generationState,
                    fieldSize = fieldSize
                )

                SurvivalWavePhase.BREAK -> updateBreakPhase(
                    waveState = updatedWaveState,
                    deltaTimeMs = remainingDeltaTimeMs,
                    generationState = generationState
                )
            }

            updatedWaveState = updateResult.waveState
            generationState = updateResult.nextGenerationState
            remainingDeltaTimeMs -= updateResult.usedDeltaTimeMs
            spawnedProjectiles += updateResult.spawnedProjectiles
        }

        return SurvivalWaveUpdateResult(
            waveState = updatedWaveState,
            spawnedProjectiles = spawnedProjectiles,
            nextGenerationState = generationState
        )
    }

    private fun updateActivePhase(
        waveState: SurvivalWaveState,
        deltaTimeMs: Int,
        generationState: ProjectileGenerationState,
        fieldSize: GameFieldSize
    ): WavePhaseUpdateResult {
        val currentWave = waveState.currentWave
        val timeUntilNextEmitterVolleyMs = waveState.emitterStates.minOf {
            it.timeUntilNextVolleyMs
        }
        val timeUntilNextEventMs = minOf(
            a = waveState.timeUntilPhaseEndMs,
            b = timeUntilNextEmitterVolleyMs
        )

        if (deltaTimeMs < timeUntilNextEventMs) {
            return WavePhaseUpdateResult(
                waveState = waveState.copy(
                    timeUntilPhaseEndMs = waveState.timeUntilPhaseEndMs - deltaTimeMs,
                    emitterStates = decreaseEmitterTimers(
                        emitterStates = waveState.emitterStates,
                        deltaTimeMs = deltaTimeMs
                    )
                ),
                usedDeltaTimeMs = deltaTimeMs,
                spawnedProjectiles = emptyList(),
                nextGenerationState = generationState
            )
        }

        val stateAtEvent = waveState.copy(
            timeUntilPhaseEndMs = waveState.timeUntilPhaseEndMs - timeUntilNextEventMs,
            emitterStates = decreaseEmitterTimers(
                emitterStates = waveState.emitterStates,
                deltaTimeMs = timeUntilNextEventMs
            )
        )

        if (stateAtEvent.timeUntilPhaseEndMs == 0) {
            return WavePhaseUpdateResult(
                waveState = stateAtEvent.copy(
                    phase = SurvivalWavePhase.BREAK,
                    timeUntilPhaseEndMs = currentWave.breakDurationMs
                ),
                usedDeltaTimeMs = timeUntilNextEventMs,
                spawnedProjectiles = emptyList(),
                nextGenerationState = generationState
            )
        }

        val volleyResult = createReadyEmitterVolleys(
            waveState = stateAtEvent,
            generationState = generationState,
            fieldSize = fieldSize
        )

        return WavePhaseUpdateResult(
            waveState = stateAtEvent.copy(
                emitterStates = volleyResult.emitterStates
            ),
            usedDeltaTimeMs = timeUntilNextEventMs,
            spawnedProjectiles = volleyResult.spawnedProjectiles,
            nextGenerationState = volleyResult.nextGenerationState
        )
    }

    private fun updateBreakPhase(
        waveState: SurvivalWaveState,
        deltaTimeMs: Int,
        generationState: ProjectileGenerationState
    ): WavePhaseUpdateResult {
        if (deltaTimeMs < waveState.timeUntilPhaseEndMs) {
            return WavePhaseUpdateResult(
                waveState = waveState.copy(
                    timeUntilPhaseEndMs = waveState.timeUntilPhaseEndMs - deltaTimeMs
                ),
                usedDeltaTimeMs = deltaTimeMs,
                spawnedProjectiles = emptyList(),
                nextGenerationState = generationState
            )
        }

        val nextWaveIndexResult = selectNextWaveIndex(
            waveState = waveState,
            randomState = generationState.randomState
        )
        val nextWave = waveState.waves[nextWaveIndexResult.value]

        return WavePhaseUpdateResult(
            waveState = waveState.copy(
                currentWaveIndex = nextWaveIndexResult.value,
                phase = SurvivalWavePhase.ACTIVE,
                timeUntilPhaseEndMs = nextWave.durationMs,
                emitterStates = nextWave.emitters.map { emitter ->
                    AttackEmitterState(
                        timeUntilNextVolleyMs = emitter.initialDelayMs
                    )
                }
            ),
            usedDeltaTimeMs = waveState.timeUntilPhaseEndMs,
            spawnedProjectiles = emptyList(),
            nextGenerationState = generationState.copy(
                randomState = nextWaveIndexResult.nextState
            )
        )
    }

    private fun decreaseEmitterTimers(
        emitterStates: List<AttackEmitterState>,
        deltaTimeMs: Int
    ): List<AttackEmitterState> {
        return emitterStates.map { emitterState ->
            emitterState.copy(
                timeUntilNextVolleyMs = emitterState.timeUntilNextVolleyMs - deltaTimeMs
            )
        }
    }

    private fun createReadyEmitterVolleys(
        waveState: SurvivalWaveState,
        generationState: ProjectileGenerationState,
        fieldSize: GameFieldSize
    ): EmitterVolleyUpdateResult {
        val updatedEmitterStates = waveState.emitterStates.toMutableList()
        val spawnedProjectiles = mutableListOf<Projectile>()
        var updatedGenerationState = generationState

        waveState.currentWave.emitters.forEachIndexed { index, emitter ->
            if (updatedEmitterStates[index].timeUntilNextVolleyMs != 0) {
                return@forEachIndexed
            }

            val patternResult = randomGenerator.pick(
                randomState = updatedGenerationState.randomState,
                values = emitter.patternOptions
            )
            updatedGenerationState = updatedGenerationState.copy(
                randomState = patternResult.nextState
            )

            val projectileCreationResult = projectileFactory.createVolley(
                pattern = patternResult.value,
                generationState = updatedGenerationState,
                fieldSize = fieldSize
            )
            updatedGenerationState = projectileCreationResult.nextGenerationState
            spawnedProjectiles += projectileCreationResult.projectiles

            updatedEmitterStates[index] = AttackEmitterState(
                timeUntilNextVolleyMs = emitter.volleyIntervalMs
            )
        }

        return EmitterVolleyUpdateResult(
            emitterStates = updatedEmitterStates,
            spawnedProjectiles = spawnedProjectiles,
            nextGenerationState = updatedGenerationState
        )
    }

    private fun selectNextWaveIndex(
        waveState: SurvivalWaveState,
        randomState: BattleRandomState
    ): RandomResult<Int> {
        if (waveState.waves.size == 1) {
            return RandomResult(
                value = waveState.currentWaveIndex,
                nextState = randomState
            )
        }

        val availableIndices = waveState.waves.indices.filter {
            it != waveState.currentWaveIndex
        }

        return randomGenerator.pick(
            randomState = randomState,
            values = availableIndices
        )
    }

    private data class EmitterVolleyUpdateResult(
        val emitterStates: List<AttackEmitterState>,
        val spawnedProjectiles: List<Projectile>,
        val nextGenerationState: ProjectileGenerationState
    )

    private data class WavePhaseUpdateResult(
        val waveState: SurvivalWaveState,
        val usedDeltaTimeMs: Int,
        val spawnedProjectiles: List<Projectile>,
        val nextGenerationState: ProjectileGenerationState
    )
}