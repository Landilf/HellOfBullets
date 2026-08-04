package ru.landilf.hellofbullets.domain.model.equipment.definition

data class StatRange(
    val min: Float,
    val max: Float
) {
    init {
        require(min > 0f) {
            "Минимальное значение характеристики должно быть положительным"
        }
        require(max >= min) {
            "Максимальное значение характеристики не может быть меньше минимального"
        }
    }

    fun growthMultiplierFor(specializationCoef: Float): Float {
        val averageValue = (min + max) / 2f

        return valueFor(specializationCoef) / averageValue
    }

    fun valueFor(specializationCoef: Float): Float {
        require(min.isFinite() && max.isFinite()) {
            "Границы диапазона характеристики должны быть конечными числами"
        }
        require(specializationCoef in -1f..1f) {
            "Коэффициент специализации должен быть в диапазоне от -1 до 1"
        }

        val normalizedCoef = (specializationCoef + 1f) / 2f

        return min + (max - min) * normalizedCoef
    }
}
