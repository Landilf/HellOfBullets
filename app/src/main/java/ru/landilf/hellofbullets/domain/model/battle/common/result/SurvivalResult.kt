package ru.landilf.hellofbullets.domain.model.battle.common.result

data class SurvivalResult(
    val time: Int,
    val reward: RewardInfo,
    val isNewRecord: Boolean,
    val leaderboardPosition: Int?,
    val leaderboardCutoffTime: Int?
)