package ru.landilf.hellofbullets.domain.model.battle.common.attackpattern

data class AttackEmitter(
    val patternOptions: List<AttackPattern>,
    val volleyIntervalMs: Int,
    val initialDelayMs: Int = volleyIntervalMs
) {
    init {
        require(patternOptions.isNotEmpty()) {
            "AttackEmitter требует хотя бы один элемент в patternOptions"
        }
        require(volleyIntervalMs > 0) {
            "Интервал между залпами в AttackEmitter должен быть больше нуля"
        }
        require(initialDelayMs >= 0) {
            "Начальная задержка в AttackEmitter не может быть отрицательной"
        }
    }
}
