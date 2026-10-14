package ru.landilf.hellofbullets.domain.model.shop

sealed interface ManualShopRefreshPreviewResult {
    data class Available(
        val refreshCost: Int
    ) : ManualShopRefreshPreviewResult

    object DailyLimitReached : ManualShopRefreshPreviewResult
}