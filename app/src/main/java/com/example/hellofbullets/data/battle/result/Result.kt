package com.example.hellofbullets.data.battle.result

import com.example.hellofbullets.data.battle.Reward

sealed class Result {
    abstract val time: Int
    abstract val reward: Reward
}