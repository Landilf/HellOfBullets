package ru.landilf.hellofbullets.domain.model.shop

import ru.landilf.hellofbullets.domain.model.equipment.Item

sealed interface PurchaseShopOfferResult {
    data class Success(
        val purchasedItem: Item,
        val spentSilverAmount: Int,
        val remainingSilverAmount: Int
    ) : PurchaseShopOfferResult

    data class InsufficientSilver(
        val requiredSilverAmount: Int,
        val currentSilverAmount: Int
    ) : PurchaseShopOfferResult

    object OfferNotFound : PurchaseShopOfferResult

    object OfferAlreadySold : PurchaseShopOfferResult
}