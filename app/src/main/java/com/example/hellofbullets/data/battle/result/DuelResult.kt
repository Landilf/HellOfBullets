package com.example.hellofbullets.data.battle.result

import com.example.hellofbullets.data.battle.Reward

data class DuelResult(
    override val survivalTime: Int,
    override val reward: Reward,
    val isVictory: Boolean,
    val bossHpLeft: Int
) : Result()
