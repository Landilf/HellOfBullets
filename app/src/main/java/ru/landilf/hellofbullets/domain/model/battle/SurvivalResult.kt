package ru.landilf.hellofbullets.domain.model.battle

import ru.landilf.hellofbullets.domain.model.leaderboard.LeaderboardRecord

data class SurvivalResult(
    val time: Int,
    val reward: RewardInfo,
    val isNewRecord: Boolean,
    val leaderboardPosition: Int?,
    val leaderboard: List<LeaderboardRecord>
)