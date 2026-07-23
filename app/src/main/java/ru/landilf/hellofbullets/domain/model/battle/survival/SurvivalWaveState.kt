package ru.landilf.hellofbullets.domain.model.battle.survival

import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.AttackEmitterState
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.AttackWave

data class SurvivalWaveState(
    val waves: List<AttackWave>,
    val currentWaveIndex: Int,
    val phase: SurvivalWavePhase,
    val timeUntilPhaseEndMs: Int,
    val emitterStates: List<AttackEmitterState>
) {
    val currentWave: AttackWave get() = waves[currentWaveIndex]

    init {
        require(waves.isNotEmpty()) {
            "SurvivalWaveState должен содержать хотя бы один элемент в waves"
        }
        require(currentWaveIndex in waves.indices) {
            "Индекс текущей волны в SurvivalWaveState должен находиться в пределах списка волн"
        }
        require(timeUntilPhaseEndMs >= 0) {
            "Время до завершения фазы в SurvivalWaveState не может быть отрицательным"
        }
        require(emitterStates.size == currentWave.emitters.size) {
            "Количество элементов в emitterStates должно совпадать с их количеством " +
                    "в текущей волне в SurvivalWaveState"
        }
    }
}
