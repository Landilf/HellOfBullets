package ru.landilf.hellofbullets.data.battle.result

import ru.landilf.hellofbullets.data.battle.Reward

sealed class Result {
    abstract val time: Int
    abstract val reward: Reward
}