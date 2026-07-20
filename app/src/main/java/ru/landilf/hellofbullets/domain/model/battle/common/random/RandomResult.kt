package ru.landilf.hellofbullets.domain.model.battle.common.random

data class RandomResult<T>(
    val value: T,
    val nextState: BattleRandomState
)