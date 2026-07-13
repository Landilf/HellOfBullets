package ru.landilf.hellofbullets.domain.usecase

import ru.landilf.hellofbullets.domain.model.battle.survival.SurvivalGameState
import ru.landilf.hellofbullets.domain.model.battle.survival.SurvivalPhase
import ru.landilf.hellofbullets.domain.model.battle.survival.SurvivalWaveState
import ru.landilf.hellofbullets.domain.model.common.Vector2
import ru.landilf.hellofbullets.domain.model.player.PlayerRuntimeState
import ru.landilf.hellofbullets.domain.model.player.PlayerStats

class CreateInitialSurvivalGameStateUseCase {
    operator fun invoke(
        playerStats: PlayerStats,
        initialWaveState: SurvivalWaveState
    ): SurvivalGameState {
        return SurvivalGameState(
            phase = SurvivalPhase.ACTIVE,
            elapsedTimeMs = 0,
            playerStats = playerStats,
            playerRuntimeState = PlayerRuntimeState(
                currentHp = playerStats.maxHp,
                position = Vector2(
                    x = 0.5f,
                    y = 0.5f
                ),
                hitRadius = 0.03f,
                isAlive = true
            ),
            survivalWaveState = initialWaveState,
            activeProjectiles = emptyList()
        )
    }
}