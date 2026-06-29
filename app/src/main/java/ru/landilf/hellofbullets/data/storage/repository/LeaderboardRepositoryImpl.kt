package ru.landilf.hellofbullets.data.storage.repository

import ru.landilf.hellofbullets.data.storage.dao.LeaderboardDao
import ru.landilf.hellofbullets.data.storage.mappers.LeaderboardRecordDomainToEntityMapper
import ru.landilf.hellofbullets.data.storage.mappers.LeaderboardRecordEntityToDomainMapper
import ru.landilf.hellofbullets.domain.model.leaderboard.LeaderboardRecord
import ru.landilf.hellofbullets.domain.repository.LeaderboardRepository
import javax.inject.Inject

class LeaderboardRepositoryImpl @Inject constructor(
    private val leaderboardDao: LeaderboardDao,
    private val entityToDomainMapper: LeaderboardRecordEntityToDomainMapper,
    private val domainToEntityMapper: LeaderboardRecordDomainToEntityMapper
) : LeaderboardRepository {
    override suspend fun getLeaderboard(): List<LeaderboardRecord> {
        return leaderboardDao.getLeaderboard().map(entityToDomainMapper::invoke)
    }

    override suspend fun getRecordByPlayerName(playerName: String): LeaderboardRecord? {
        return leaderboardDao.getRecordByPlayerName(playerName)?.let(entityToDomainMapper::invoke)
    }

    override suspend fun upsertRecord(record: LeaderboardRecord) {
        leaderboardDao.upsert(domainToEntityMapper(record))
    }

    override suspend fun clearLeaderboard() {
        leaderboardDao.clearLeaderboard()
    }
}