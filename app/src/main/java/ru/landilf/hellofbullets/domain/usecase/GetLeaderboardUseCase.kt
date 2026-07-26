package ru.landilf.hellofbullets.domain.usecase

import ru.landilf.hellofbullets.domain.model.leaderboard.LeaderboardRecord
import ru.landilf.hellofbullets.domain.repository.LeaderboardRepository
import javax.inject.Inject

class GetLeaderboardUseCase @Inject constructor(
    private val leaderboardRepository: LeaderboardRepository
) {
    suspend operator fun invoke(): List<LeaderboardRecord> {
        return leaderboardRepository.getLeaderboard()
    }
}