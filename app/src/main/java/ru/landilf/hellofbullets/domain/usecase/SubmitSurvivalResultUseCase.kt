package ru.landilf.hellofbullets.domain.usecase

import ru.landilf.hellofbullets.domain.model.battle.common.result.SurvivalResult
import ru.landilf.hellofbullets.domain.model.leaderboard.LeaderboardRecord
import ru.landilf.hellofbullets.domain.model.player.PlayerState
import ru.landilf.hellofbullets.domain.repository.LeaderboardRepository
import ru.landilf.hellofbullets.domain.repository.PlayerRepository

class SubmitSurvivalResultUseCase(
    private val playerRepository: PlayerRepository,
    private val leaderboardRepository: LeaderboardRepository,
    private val calculateSurvivalRewardUseCase: CalculateSurvivalRewardUseCase
) {
    suspend operator fun invoke(time: Int, playerLevel: Int): SurvivalResult {
        val playerState = playerRepository.getPlayerState()
            ?: error("Player state is not initialized")
        val reward = calculateSurvivalRewardUseCase(time, playerLevel)
        val currentRecord =
            leaderboardRepository.getRecordByPlayerName(playerState.playerProfile.name)
        val isNewRecord = currentRecord == null || time > currentRecord.time

        if (isNewRecord) {
            leaderboardRepository.upsertRecord(
                LeaderboardRecord(
                    playerName = playerState.playerProfile.name,
                    time = time
                )
            )
        }

        val leaderboard = leaderboardRepository.getLeaderboard()
        val position = leaderboard.indexOfFirst { it.playerName == playerState.playerProfile.name }
            .takeIf { it >= 0 }
            ?.plus(1)
        val updatedState = PlayerState(
            playerProfile = playerState.playerProfile.copy(
                expAmount = playerState.playerProfile.expAmount + reward.exp,
                silverAmount = playerState.playerProfile.silverAmount + reward.silver
            ),
            playerBuild = playerState.playerBuild,
            inventory = playerState.inventory
        )

        playerRepository.savePlayerState(updatedState)

        return SurvivalResult(
            time = time,
            reward = reward,
            isNewRecord = isNewRecord,
            leaderboardPosition = position,
            leaderboard = leaderboard
        )
    }
}