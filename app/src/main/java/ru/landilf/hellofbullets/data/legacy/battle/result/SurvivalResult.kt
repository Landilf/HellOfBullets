package ru.landilf.hellofbullets.data.legacy.battle.result

import ru.landilf.hellofbullets.data.legacy.battle.Reward

data class SurvivalResult(
    override val time: Int,
    override val reward: Reward,
    val isNewRecord: Boolean,
    val leaderboardPosition: Int
) : Result()
