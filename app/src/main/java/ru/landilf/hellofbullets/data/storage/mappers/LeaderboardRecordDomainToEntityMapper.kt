package ru.landilf.hellofbullets.data.storage.mappers

import ru.landilf.hellofbullets.data.storage.entities.leaderboard.LeaderboardRecordEntity
import ru.landilf.hellofbullets.domain.model.leaderboard.LeaderboardRecord

class LeaderboardRecordDomainToEntityMapper {
    operator fun invoke(domain: LeaderboardRecord): LeaderboardRecordEntity {
        return LeaderboardRecordEntity(
            playerName = domain.playerName,
            time = domain.time
        )
    }
}