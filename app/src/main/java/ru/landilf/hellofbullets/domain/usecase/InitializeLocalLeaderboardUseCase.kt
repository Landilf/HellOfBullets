package ru.landilf.hellofbullets.domain.usecase

import ru.landilf.hellofbullets.domain.model.leaderboard.LeaderboardRecord
import ru.landilf.hellofbullets.domain.repository.LeaderboardRepository
import javax.inject.Inject

class InitializeLocalLeaderboardUseCase @Inject constructor(
    private val leaderboardRepository: LeaderboardRepository
) {
    suspend operator fun invoke() {
        val mockPlayerNames = mockRecords.map { it.playerName }

        val existingPlayerNames = leaderboardRepository.getExistingPlayerNames(
            playerNames = mockPlayerNames
        )

        val recordsToAdd = mockRecords
            .filter { it.playerName !in existingPlayerNames }

        for (record in recordsToAdd) {
            leaderboardRepository.upsertRecord(record)
        }
    }

    private companion object {
        const val MAX_RECORDS = 20

        val mockRecords = List(MAX_RECORDS) { index ->
            LeaderboardRecord(
                playerName = "Rival ${index + 1}",
                time = (MAX_RECORDS - index) * 10
            )
        }
    }
}