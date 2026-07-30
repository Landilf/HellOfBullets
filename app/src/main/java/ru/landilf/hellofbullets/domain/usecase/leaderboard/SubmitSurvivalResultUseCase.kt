package ru.landilf.hellofbullets.domain.usecase.leaderboard

import ru.landilf.hellofbullets.domain.model.battle.common.result.SurvivalResult
import ru.landilf.hellofbullets.domain.model.leaderboard.LeaderboardRecord
import ru.landilf.hellofbullets.domain.repository.LeaderboardRepository
import ru.landilf.hellofbullets.domain.usecase.player.ApplyPlayerRewardUseCase
import ru.landilf.hellofbullets.domain.usecase.player.GetOrCreatePlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.survival.CalculateSurvivalRewardUseCase
import javax.inject.Inject

class SubmitSurvivalResultUseCase @Inject constructor(
    private val getOrCreatePlayerStateUseCase: GetOrCreatePlayerStateUseCase,
    private val leaderboardRepository: LeaderboardRepository,
    private val calculateSurvivalRewardUseCase: CalculateSurvivalRewardUseCase,
    private val applyPlayerRewardUseCase: ApplyPlayerRewardUseCase
) {
    suspend operator fun invoke(time: Int): SurvivalResult {
        val playerState = getOrCreatePlayerStateUseCase()
        val playerRecordId = playerState.playerProfile.id.toString()

        val reward = calculateSurvivalRewardUseCase(
            time = time,
            playerLevel = playerState.playerProfile.level
        )

        val currentRecord =
            leaderboardRepository.getRecordById(playerRecordId)
        val isNewRecord = currentRecord == null || time > currentRecord.time

        if (isNewRecord) {
            leaderboardRepository.upsertRecord(
                LeaderboardRecord(
                    id = playerRecordId,
                    playerName = playerState.playerProfile.name,
                    time = time
                )
            )
        }

        val leaderboard = leaderboardRepository.getLeaderboard()
        val position = leaderboard.indexOfFirst { record ->
            record.id == playerRecordId
        }.takeIf { it >= 0 }
            ?.plus(1)
        val leaderboardCutoffTime = leaderboard.lastOrNull()?.time

        applyPlayerRewardUseCase(
            playerState = playerState,
            reward = reward
        )

        return SurvivalResult(
            time = time,
            reward = reward,
            isNewRecord = isNewRecord,
            leaderboardPosition = position,
            leaderboardCutoffTime = leaderboardCutoffTime
        )
    }
}