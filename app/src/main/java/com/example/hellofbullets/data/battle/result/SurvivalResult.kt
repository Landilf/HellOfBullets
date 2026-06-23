package com.example.hellofbullets.data.battle.result

import com.example.hellofbullets.data.battle.Reward

data class SurvivalResult(
    override val survivalTime: Int,
    override val reward: Reward,
    val isNewRecord: Boolean,
    val leaderboardPosition: Int
) : Result()
