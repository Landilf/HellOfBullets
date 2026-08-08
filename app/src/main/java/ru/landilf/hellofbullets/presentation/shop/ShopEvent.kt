package ru.landilf.hellofbullets.presentation.shop

sealed interface ShopEvent {
    data object OfferPurchased : ShopEvent
    data object InsufficientSilver : ShopEvent
    data object OfferUnavailable : ShopEvent
    data object ShopRefreshed : ShopEvent
    data object ManualRefreshLimitReached : ShopEvent
}