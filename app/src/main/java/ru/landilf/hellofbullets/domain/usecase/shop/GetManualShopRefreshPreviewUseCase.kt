package ru.landilf.hellofbullets.domain.usecase.shop

import ru.landilf.hellofbullets.domain.engine.shop.ShopManualRefreshCostCalculator
import ru.landilf.hellofbullets.domain.model.shop.ManualShopRefreshPreviewResult
import ru.landilf.hellofbullets.domain.model.shop.ShopState
import ru.landilf.hellofbullets.domain.usecase.player.GetOrCreatePlayerStateUseCase
import javax.inject.Inject

class GetManualShopRefreshPreviewUseCase @Inject constructor(
    private val getOrRefreshShopStateUseCase: GetOrRefreshShopStateUseCase,
    private val getOrCreatePlayerStateUseCase: GetOrCreatePlayerStateUseCase,
    private val shopManualRefreshCostCalculator: ShopManualRefreshCostCalculator
) {
    suspend operator fun invoke(): ManualShopRefreshPreviewResult {
        val shopState = getOrRefreshShopStateUseCase()

        if (shopState.manualRefreshCount >= ShopState.MAX_MANUAL_REFRESH_COUNT) {
            return ManualShopRefreshPreviewResult.DailyLimitReached
        }

        val playerState = getOrCreatePlayerStateUseCase()
        val refreshCost = shopManualRefreshCostCalculator(
            playerLevel = playerState.playerProfile.level,
            completedRefreshCount = shopState.manualRefreshCount
        )

        return ManualShopRefreshPreviewResult.Available(
            refreshCost = refreshCost,
            remainingRefreshCount =
                ShopState.MAX_MANUAL_REFRESH_COUNT - shopState.manualRefreshCount - 1
        )
    }
}