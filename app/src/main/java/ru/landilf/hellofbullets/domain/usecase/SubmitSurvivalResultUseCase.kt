package ru.landilf.hellofbullets.domain.usecase

import ru.landilf.hellofbullets.domain.model.battle.common.result.SurvivalResult
import ru.landilf.hellofbullets.domain.model.leaderboard.LeaderboardRecord
import ru.landilf.hellofbullets.domain.model.player.PlayerState
import ru.landilf.hellofbullets.domain.repository.LeaderboardRepository
import javax.inject.Inject

class SubmitSurvivalResultUseCase @Inject constructor(
    private val getOrCreatePlayerStateUseCase: GetOrCreatePlayerStateUseCase,
    private val leaderboardRepository: LeaderboardRepository,
    private val calculateSurvivalRewardUseCase: CalculateSurvivalRewardUseCase,
    private val savePlayerStateUseCase: SavePlayerStateUseCase,
    private val initializeLocalLeaderboardUseCase: InitializeLocalLeaderboardUseCase
) {
    suspend operator fun invoke(time: Int): SurvivalResult {
        initializeLocalLeaderboardUseCase()

        val playerState = getOrCreatePlayerStateUseCase()
        val reward = calculateSurvivalRewardUseCase(
            time = time,
            playerLevel = playerState.playerProfile.level
        )
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
        val leaderboardCutoffTime = leaderboard.lastOrNull()?.time
        val updatedState = PlayerState(
            playerProfile = playerState.playerProfile.copy(
                expAmount = playerState.playerProfile.expAmount + reward.exp,
                silverAmount = playerState.playerProfile.silverAmount + reward.silver
            ),
            playerBuild = playerState.playerBuild,
            inventory = playerState.inventory
        )

        savePlayerStateUseCase(updatedState)

        return SurvivalResult(
            time = time,
            reward = reward,
            isNewRecord = isNewRecord,
            leaderboardPosition = position,
            leaderboardCutoffTime = leaderboardCutoffTime
        )
    }
}