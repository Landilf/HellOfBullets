package ru.landilf.hellofbullets.data.legacy.battle.result

import ru.landilf.hellofbullets.data.legacy.battle.Reward

data class DuelResult(
    override val time: Int,
    override val reward: Reward,
    val isVictory: Boolean,
    val bossHpLeft: Int
) : Result()
