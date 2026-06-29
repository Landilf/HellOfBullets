package ru.landilf.hellofbullets.data.storage.mappers

import ru.landilf.hellofbullets.data.storage.entities.leaderboard.LeaderboardRecordEntity
import ru.landilf.hellofbullets.domain.model.leaderboard.LeaderboardRecord

class LeaderboardRecordEntityToDomainMapper {
    operator fun invoke(entity: LeaderboardRecordEntity): LeaderboardRecord {
        return LeaderboardRecord(
            playerName = entity.playerName,
            time = entity.time
        )
    }
}