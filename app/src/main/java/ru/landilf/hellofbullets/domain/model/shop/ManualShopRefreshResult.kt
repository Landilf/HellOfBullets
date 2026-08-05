package ru.landilf.hellofbullets.domain.model.shop

sealed interface ManualShopRefreshResult {
    data class Success(
        val shopState: ShopState,
        val spentSilverAmount: Int,
        val remainingSilverAmount: Int
    ) : ManualShopRefreshResult

    data class InsufficientSilver(
        val requiredSilverAmount: Int,
        val currentSilverAmount: Int
    ) : ManualShopRefreshResult

    object DailyLimitReached : ManualShopRefreshResult
}