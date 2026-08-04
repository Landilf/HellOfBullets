package ru.landilf.hellofbullets.domain.model.equipment

data class EquipmentQualityWeight(
    val quality: EquipmentQuality,
    val weight: Int
) {
    init {
        require(weight > 0) {
            "Вес качества экипировки должен быть положительным"
        }
        require(quality.isAvailableInShop) {
            "Недоступное в магазине качество не может входить в распределение"
        }
    }
}
