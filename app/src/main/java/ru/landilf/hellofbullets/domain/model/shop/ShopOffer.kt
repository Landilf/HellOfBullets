package ru.landilf.hellofbullets.domain.model.shop

import ru.landilf.hellofbullets.domain.model.equipment.Item

data class ShopOffer(
    val item: Item,
    val purchasePrice: Int
) {
    init {
        require(purchasePrice > 0) {
            "Цена товара в магазине должна быть положительной"
        }
    }
}
