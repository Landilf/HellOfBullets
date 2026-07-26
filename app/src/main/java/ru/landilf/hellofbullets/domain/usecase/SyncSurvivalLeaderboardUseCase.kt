package ru.landilf.hellofbullets.domain.usecase

import ru.landilf.hellofbullets.domain.repository.LeaderboardRepository
import ru.landilf.hellofbullets.domain.repository.OnlineLeaderboardRepository
import javax.inject.Inject

class SyncSurvivalLeaderboardUseCase @Inject constructor(
    private val getOrCreatePlayerStateUseCase: GetOrCreatePlayerStateUseCase,
    private val leaderboardRepository: LeaderboardRepository,
    private val onlineLeaderboardRepository: OnlineLeaderboardRepository
) {
    suspend operator fun invoke() {
        val playerState = getOrCreatePlayerStateUseCase()
        val localPlayerRecordId = playerState.playerProfile.id.toString()
        val onlinePlayerId = onlineLeaderboardRepository.getOrCreatePlayerId()

        val localPlayerRecord = leaderboardRepository.getRecordById(
            id = localPlayerRecordId
        )

        if (localPlayerRecord != null) {
            onlineLeaderboardRepository.submitSurvivalRecord(
                playerId = onlinePlayerId,
                playerName = localPlayerRecord.playerName,
                time = localPlayerRecord.time
            )
        }

        val onlineRecords = onlineLeaderboardRepository.getTopSurvivalRecords(
            limit = MAX_RECORDS
        )

        if (onlineRecords.size < MAX_RECORDS) {
            return
        }

        val cachedOnlineRecords = onlineRecords.map { record ->
            if (record.id == onlinePlayerId) {
                record.copy(
                    id = localPlayerRecordId
                )
            } else {
                record
            }
        }

        val recordsToCache = buildList {
            addAll(cachedOnlineRecords)

            // Личный рекорд остаётся локально, даже если не входит в таблицу рекордов
            if (
                localPlayerRecord != null && cachedOnlineRecords.none {
                    it.id == localPlayerRecordId
                }
            ) {
                add(localPlayerRecord)
            }
        }

        leaderboardRepository.replaceLeaderboard(recordsToCache)
    }

    private companion object {
        const val MAX_RECORDS = 20
    }
}