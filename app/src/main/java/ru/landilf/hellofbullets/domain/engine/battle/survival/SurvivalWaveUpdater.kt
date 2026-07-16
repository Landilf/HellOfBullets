package ru.landilf.hellofbullets.domain.engine.battle.survival

import ru.landilf.hellofbullets.domain.engine.battle.common.ProjectileFactory
import ru.landilf.hellofbullets.domain.model.battle.survival.SurvivalWaveState
import ru.landilf.hellofbullets.domain.model.battle.survival.SurvivalWaveUpdateResult
import javax.inject.Inject

class SurvivalWaveUpdater @Inject constructor(
    private val projectileFactory: ProjectileFactory
) {

    fun update(
        waveState: SurvivalWaveState?,
        deltaTimeMs: Int
    ): SurvivalWaveUpdateResult {
        if (waveState == null) {
            return SurvivalWaveUpdateResult(
                waveState = null,
                spawnedProjectiles = emptyList()
            )
        }

        val updatedElapsedWaveTimeMs = waveState.elapsedWaveTimeMs + deltaTimeMs
        val updatedTimeUntilNextVolleyMs = waveState.timeUntilNextVolleyMs - deltaTimeMs

        if (updatedTimeUntilNextVolleyMs > 0) {
            return SurvivalWaveUpdateResult(
                waveState = waveState.copy(
                    elapsedWaveTimeMs = updatedElapsedWaveTimeMs,
                    timeUntilNextVolleyMs = updatedTimeUntilNextVolleyMs
                ),
                spawnedProjectiles = emptyList()
            )
        }

        val currentPattern = waveState.currentWave.patterns[waveState.currentPatternIndex]
        val projectiles = projectileFactory.createVolley(currentPattern)

        return SurvivalWaveUpdateResult(
            waveState = waveState.copy(
                elapsedWaveTimeMs = updatedElapsedWaveTimeMs,
                timeUntilNextVolleyMs = currentPattern.spawnIntervalMs
            ),
            spawnedProjectiles = projectiles
        )
    }
}