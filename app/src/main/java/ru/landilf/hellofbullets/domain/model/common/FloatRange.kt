package ru.landilf.hellofbullets.domain.model.common

data class FloatRange(
    val min: Float,
    val max: Float
) {
    init {
        require(min.isFinite() && max.isFinite()) {
            "Границы FloatRange должны являться конечным числом"
        }
        require(min <= max) {
            "Левая граница FloatRange не должна быть больше правой"
        }
    }
}
