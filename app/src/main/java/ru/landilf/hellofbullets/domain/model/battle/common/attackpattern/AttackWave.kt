package ru.landilf.hellofbullets.domain.model.battle.common.attackpattern

data class AttackWave(
    val id: Long,
    val emitters: List<AttackEmitter>,
    val durationMs: Int,
    val breakDurationMs: Int
) {
    init {
        require(emitters.isNotEmpty()) {
            "AttackWave должен содержать хотя бы один элемент в emitters"
        }
        require(durationMs > 0) {
            "Длительность волны в AttackWave должна быть больше нуля"
        }
        require(breakDurationMs >= 0) {
            "Длительность перерыва в AttackWave не может быть отрицательной"
        }
    }
}