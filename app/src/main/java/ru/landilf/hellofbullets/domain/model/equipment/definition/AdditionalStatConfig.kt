package ru.landilf.hellofbullets.domain.model.equipment.definition

data class AdditionalStatConfig(
    val initialValueCoef: Float,
    val levelGrowthMultiplier: Float
) {
    init {
        require(initialValueCoef > 0f) {
            "Коэффициент начального значения дополнительной характеристики должен быть положительным"
        }
        require(levelGrowthMultiplier > 0f) {
            "Коэффициент прироста дополнительной характеристики должен быть положительным"
        }
    }
}
