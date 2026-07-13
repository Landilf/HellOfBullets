package ru.landilf.hellofbullets.domain.usecase

import ru.landilf.hellofbullets.domain.engine.battle.common.PlayerCollisionChecker
import ru.landilf.hellofbullets.domain.engine.battle.common.ProjectileMovementUpdater
import ru.landilf.hellofbullets.domain.engine.battle.survival.SurvivalWaveUpdater
import ru.landilf.hellofbullets.domain.model.battle.survival.SurvivalGameState
import ru.landilf.hellofbullets.domain.model.battle.survival.SurvivalPhase

class UpdateSurvivalGameStateUseCase(
    private val survivalWaveUpdater: SurvivalWaveUpdater,
    private val projectileMovementUpdater: ProjectileMovementUpdater,
    private val playerCollisionChecker: PlayerCollisionChecker
) {
    operator fun invoke(
        gameState: SurvivalGameState,
        deltaTimeMs: Int
    ): SurvivalGameState {
        if (gameState.phase != SurvivalPhase.ACTIVE) {
            return gameState
        }

        val waveUpdateResult = survivalWaveUpdater.update(
            waveState = gameState.survivalWaveState,
            deltaTimeMs = deltaTimeMs
        )

        val projectilesAfterSpawn =
            gameState.activeProjectiles + waveUpdateResult.spawnedProjectiles

        val updatedProjectiles = projectileMovementUpdater.update(
            projectiles = projectilesAfterSpawn,
            deltaTimeMs = deltaTimeMs
        )

        val hasCollision = playerCollisionChecker.hasCollision(
            player = gameState.playerRuntimeState,
            projectiles = updatedProjectiles
        )

        val updatedPlayerRuntimeState = if (hasCollision) {
            gameState.playerRuntimeState.copy(
                currentHp = 0,
                isAlive = false
            )
        } else {
            gameState.playerRuntimeState
        }

        return gameState.copy(
            phase = if (hasCollision) SurvivalPhase.FINISHED else gameState.phase,
            elapsedTimeMs = gameState.elapsedTimeMs + deltaTimeMs,
            playerRuntimeState = updatedPlayerRuntimeState,
            survivalWaveState = waveUpdateResult.waveState,
            activeProjectiles = updatedProjectiles
        )
    }
}