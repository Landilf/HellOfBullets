package com.landilf.hellofbullets.data.battle.result

import com.landilf.hellofbullets.data.battle.Reward

data class DuelResult(
    override val time: Int,
    override val reward: Reward,
    val isVictory: Boolean,
    val bossHpLeft: Int
) : Result()
