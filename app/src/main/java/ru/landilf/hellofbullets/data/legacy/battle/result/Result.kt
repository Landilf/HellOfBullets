package ru.landilf.hellofbullets.data.legacy.battle.result

import ru.landilf.hellofbullets.data.legacy.battle.Reward

sealed class Result {
    abstract val time: Int
    abstract val reward: Reward
}