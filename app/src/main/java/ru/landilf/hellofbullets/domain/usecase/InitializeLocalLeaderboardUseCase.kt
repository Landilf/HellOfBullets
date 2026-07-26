package ru.landilf.hellofbullets.domain.usecase

import ru.landilf.hellofbullets.domain.model.leaderboard.LeaderboardRecord
import ru.landilf.hellofbullets.domain.repository.LeaderboardRepository
import javax.inject.Inject

class InitializeLocalLeaderboardUseCase @Inject constructor(
    private val leaderboardRepository: LeaderboardRepository
) {
    suspend operator fun invoke() {
        val mockRecordIds = mockRecords.map { it.id }

        val existingRecordIds = leaderboardRepository.getExistingRecordIds(
            ids = mockRecordIds
        )

        val recordsToAdd = mockRecords.filter { record ->
            record.id !in existingRecordIds
        }

        for (record in recordsToAdd) {
            leaderboardRepository.upsertRecord(record)
        }
    }

    private companion object {
        const val MAX_RECORDS = 20

        val mockRecords = List(MAX_RECORDS) { index ->
            LeaderboardRecord(
                id = "mock-rival-${index + 1}",
                playerName = "Rival ${index + 1}",
                time = (MAX_RECORDS - index) * 10
            )
        }
    }
}