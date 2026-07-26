package ru.landilf.hellofbullets.data.storage.mappers

import ru.landilf.hellofbullets.data.storage.entities.leaderboard.LeaderboardRecordEntity
import ru.landilf.hellofbullets.domain.model.leaderboard.LeaderboardRecord
import javax.inject.Inject

class LeaderboardRecordDomainToEntityMapper @Inject constructor() {
    operator fun invoke(domain: LeaderboardRecord): LeaderboardRecordEntity {
        return LeaderboardRecordEntity(
            id = domain.id,
            playerName = domain.playerName,
            time = domain.time
        )
    }
}