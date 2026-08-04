package ru.landilf.hellofbullets.domain.model.equipment.definition

data class StatRange(
    val min: Float,
    val max: Float
) {
    init {
        require(min.isFinite() && max.isFinite()) {
            "Границы диапазона характеристики должны быть конечными числами"
        }
        require(min > 0f) {
            "Минимальное значение характеристики должно быть положительным"
        }
        require(max >= min) {
            "Максимальное значение характеристики не может быть меньше минимального"
        }
    }

    fun growthMultiplierFor(specializationCoef: Float): Float {
        require(specializationCoef in -1f..1f) {
            "Коэффициент специализации должен быть в диапазоне от -1 до 1"
        }

        val averageValue = (min + max) / 2f

        return valueFor(specializationCoef) / averageValue
    }

    fun valueFor(specializationCoef: Float): Float {
        val normalizedCoef = (specializationCoef + 1f) / 2f

        return min + (max - min) * normalizedCoef
    }

    fun scaleBy(coef: Float): StatRange {
        require(coef.isFinite() && coef > 0f) {
            "Коэффициент масштабирования диапазона должен быть положительным конечным числом"
        }

        return StatRange(
            min = min * coef,
            max = max * coef
        )
    }
}
