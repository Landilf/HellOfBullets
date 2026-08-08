package ru.landilf.hellofbullets.presentation.shop

sealed interface ShopAction {
    data class OnOfferClick(
        val itemId: Long
    ) : ShopAction

    data object OnOfferDetailsDismiss : ShopAction
    data object OnPurchaseSelectedOfferClick : ShopAction
    data object OnManualRefreshClick : ShopAction
    data object OnManualRefreshConfirmationDismiss : ShopAction
    data object OnManualRefreshConfirmClick : ShopAction
}