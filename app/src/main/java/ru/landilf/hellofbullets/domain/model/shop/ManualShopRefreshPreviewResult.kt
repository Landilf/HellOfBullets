package ru.landilf.hellofbullets.domain.model.shop

sealed interface ManualShopRefreshPreviewResult {
    data class Available(
        val refreshCost: Int,
        val remainingRefreshCount: Int
    ) : ManualShopRefreshPreviewResult

    object DailyLimitReached : ManualShopRefreshPreviewResult
}