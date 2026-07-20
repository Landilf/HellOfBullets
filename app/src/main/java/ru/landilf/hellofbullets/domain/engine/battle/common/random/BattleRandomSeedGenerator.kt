package ru.landilf.hellofbullets.domain.engine.battle.common.random

import javax.inject.Inject
import kotlin.random.Random

class BattleRandomSeedGenerator @Inject constructor() {
    fun nextSeed(): Long {
        return Random.nextLong()
    }
}