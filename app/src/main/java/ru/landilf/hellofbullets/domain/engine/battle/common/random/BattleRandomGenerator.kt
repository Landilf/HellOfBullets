package ru.landilf.hellofbullets.domain.engine.battle.common.random

import ru.landilf.hellofbullets.domain.model.battle.common.random.BattleRandomState
import ru.landilf.hellofbullets.domain.model.battle.common.random.RandomResult
import ru.landilf.hellofbullets.domain.model.common.FloatRange
import javax.inject.Inject
import kotlin.random.Random

class BattleRandomGenerator @Inject constructor() {
    fun nextFloat(
        randomState: BattleRandomState,
        range: FloatRange
    ): RandomResult<Float> {
        val random = Random(randomState.seed)
        val value = range.min + (range.max - range.min) * random.nextFloat()

        return RandomResult(
            value = value,
            nextState = BattleRandomState(
                seed = random.nextLong()
            )
        )
    }

    fun <T> pick(
        randomState: BattleRandomState,
        values: List<T>
    ): RandomResult<T> {
        require(values.isNotEmpty()) {
            "Случайный элемент не может быть выбран из пустого списка"
        }

        val random = Random(randomState.seed)

        return RandomResult(
            value = values[random.nextInt(values.size)],
            nextState = BattleRandomState(
                seed = random.nextLong()
            )
        )
    }
}