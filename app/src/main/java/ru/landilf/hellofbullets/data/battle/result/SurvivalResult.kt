package ru.landilf.hellofbullets.data.battle.result

import ru.landilf.hellofbullets.data.battle.Reward

data class SurvivalResult(
    override val time: Int,
    override val reward: Reward,
    val isNewRecord: Boolean,
    val leaderboardPosition: Int
) : Result()
