package ru.landilf.hellofbullets.domain.usecase.shop

import ru.landilf.hellofbullets.domain.model.shop.ShopState
import ru.landilf.hellofbullets.domain.repository.ShopRepository
import ru.landilf.hellofbullets.domain.usecase.player.GetOrCreatePlayerStateUseCase
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

class GetOrRefreshShopStateUseCase @Inject constructor(
    private val shopRepository: ShopRepository,
    private val getOrCreatePlayerStateUseCase: GetOrCreatePlayerStateUseCase,
    private val generateShopOffersUseCase: GenerateShopOffersUseCase,
    private val clock: Clock
) {
    suspend operator fun invoke(): ShopState {
        val currentDate = LocalDate.now(clock)
        val savedShopState = shopRepository.getShopState()

        if (savedShopState?.lastAutomaticRefreshDate == currentDate) {
            return savedShopState
        }

        val playerState = getOrCreatePlayerStateUseCase()
        val refreshShopState = ShopState(
            offers = generateShopOffersUseCase(
                playerLevel = playerState.playerProfile.level
            ),
            lastAutomaticRefreshDate = currentDate,
            manualRefreshCount = 0
        )

        shopRepository.saveShopState(refreshShopState)

        return refreshShopState
    }
}