package ru.landilf.hellofbullets.domain.usecase

import ru.landilf.hellofbullets.domain.model.leaderboard.LeaderboardRecord
import ru.landilf.hellofbullets.domain.repository.LeaderboardRepository

class GetLeaderboardUseCase(
    private val leaderboardRepository: LeaderboardRepository
) {
    suspend operator fun invoke(): List<LeaderboardRecord> {
        return leaderboardRepository.getLeaderboard()
    }
}