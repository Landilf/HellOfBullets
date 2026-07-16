package ru.landilf.hellofbullets.domain.usecase

import ru.landilf.hellofbullets.domain.model.battle.survival.SurvivalGameState
import ru.landilf.hellofbullets.domain.model.battle.survival.SurvivalPhase
import ru.landilf.hellofbullets.domain.model.battle.survival.SurvivalWaveState
import ru.landilf.hellofbullets.domain.model.common.GameFieldSize
import ru.landilf.hellofbullets.domain.model.common.Vector2
import ru.landilf.hellofbullets.domain.model.player.PlayerRuntimeState
import ru.landilf.hellofbullets.domain.model.player.PlayerStats
import javax.inject.Inject

class CreateInitialSurvivalGameStateUseCase @Inject constructor() {
    operator fun invoke(
        playerStats: PlayerStats,
        initialWaveState: SurvivalWaveState,
        fieldSize: GameFieldSize
    ): SurvivalGameState {
        return SurvivalGameState(
            phase = SurvivalPhase.ACTIVE,
            elapsedTimeMs = 0,
            playerStats = playerStats,
            playerRuntimeState = PlayerRuntimeState(
                currentHp = playerStats.maxHp,
                position = Vector2(
                    x = fieldSize.width / 2f,
                    y = fieldSize.height / 2f
                ),
                hitRadius = 3f,
                isAlive = true
            ),
            survivalWaveState = initialWaveState,
            activeProjectiles = emptyList(),
            fieldSize = fieldSize
        )
    }
}