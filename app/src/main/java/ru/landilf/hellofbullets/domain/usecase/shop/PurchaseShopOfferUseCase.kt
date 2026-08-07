package ru.landilf.hellofbullets.domain.usecase.shop

import ru.landilf.hellofbullets.domain.model.shop.PurchaseShopOfferResult
import ru.landilf.hellofbullets.domain.repository.ShopRepository
import ru.landilf.hellofbullets.domain.usecase.player.GetOrCreatePlayerStateUseCase
import javax.inject.Inject

class PurchaseShopOfferUseCase @Inject constructor(
    private val getOrRefreshShopStateUseCase: GetOrRefreshShopStateUseCase,
    private val getOrCreatePlayerStateUseCase: GetOrCreatePlayerStateUseCase,
    private val shopRepository: ShopRepository
) {
    suspend operator fun invoke(itemId: Long): PurchaseShopOfferResult {
        getOrRefreshShopStateUseCase()

        val playerState = getOrCreatePlayerStateUseCase()

        return shopRepository.purchaseOffer(
            playerId = playerState.playerProfile.id,
            itemId = itemId
        )
    }
}