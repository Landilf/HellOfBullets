package com.landilf.hellofbullets.domain.usecase

import com.landilf.hellofbullets.domain.model.leaderboard.LeaderboardRecord
import com.landilf.hellofbullets.domain.repository.LeaderboardRepository

class GetLeaderboardUseCase(
    private val leaderboardRepository: LeaderboardRepository
) {
    suspend operator fun invoke(): List<LeaderboardRecord> {
        return leaderboardRepository.getLeaderboard()
    }
}