package ru.landilf.hellofbullets.domain.usecase.shop

import ru.landilf.hellofbullets.domain.engine.shop.ShopManualRefreshCostCalculator
import ru.landilf.hellofbullets.domain.model.shop.ManualShopRefreshResult
import ru.landilf.hellofbullets.domain.model.shop.ShopState
import ru.landilf.hellofbullets.domain.repository.ShopRepository
import ru.landilf.hellofbullets.domain.usecase.player.GetOrCreatePlayerStateUseCase
import javax.inject.Inject

class RefreshShopManuallyUseCase @Inject constructor(
    private val getOrRefreshShopStateUseCase: GetOrRefreshShopStateUseCase,
    private val getOrCreatePlayerStateUseCase: GetOrCreatePlayerStateUseCase,
    private val generateShopOffersUseCase: GenerateShopOffersUseCase,
    private val shopManualRefreshCostCalculator: ShopManualRefreshCostCalculator,
    private val shopRepository: ShopRepository
) {
    suspend operator fun invoke(): ManualShopRefreshResult {
        val currentShopState = getOrRefreshShopStateUseCase()

        if (currentShopState.manualRefreshCount >= ShopState.MAX_MANUAL_REFRESH_COUNT) {
            return ManualShopRefreshResult.DailyLimitReached
        }

        val playerState = getOrCreatePlayerStateUseCase()
        val playerProfile = playerState.playerProfile
        val refreshCost = shopManualRefreshCostCalculator(
            playerLevel = playerProfile.level,
            completedRefreshCount = currentShopState.manualRefreshCount
        )

        if (playerProfile.silverAmount < refreshCost) {
            return ManualShopRefreshResult.InsufficientSilver(
                requiredSilverAmount = refreshCost,
                currentSilverAmount = playerProfile.silverAmount
            )
        }

        val remainingSilverAmount = playerProfile.silverAmount - refreshCost
        val refreshedShopState = currentShopState.copy(
            offers = generateShopOffersUseCase(
                playerLevel = playerProfile.level
            ),
            manualRefreshCount = currentShopState.manualRefreshCount + 1
        )

        shopRepository.applyManualRefresh(
            playerId = playerProfile.id,
            updatedSilverAmount = remainingSilverAmount,
            refreshedShopState = refreshedShopState
        )

        return ManualShopRefreshResult.Success(
            shopState = refreshedShopState,
            spentSilverAmount = refreshCost,
            remainingSilverAmount = remainingSilverAmount
        )
    }
}