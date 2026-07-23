package ru.landilf.hellofbullets.domain.model.battle.common.attackpattern

data class AttackEmitterState(
    val timeUntilNextVolleyMs: Int
) {
    init {
        require(timeUntilNextVolleyMs >= 0) {
            "Время до следующего залпа в AttackEmitterState не может быть отрицательным"
        }
    }
}