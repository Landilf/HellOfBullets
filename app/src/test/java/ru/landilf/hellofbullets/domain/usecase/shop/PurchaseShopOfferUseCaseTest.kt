package ru.landilf.hellofbullets.domain.usecase.shop

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.landilf.hellofbullets.domain.model.shop.PurchaseShopOfferResult
import ru.landilf.hellofbullets.domain.model.shop.ShopState
import ru.landilf.hellofbullets.domain.usecase.FakePlayerRepository
import ru.landilf.hellofbullets.domain.usecase.FakeShopRepository
import ru.landilf.hellofbullets.domain.usecase.player.GetOrCreatePlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.player.LoadPlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.player.SavePlayerStateUseCase
import java.time.LocalDate

class PurchaseShopOfferUseCaseTest {

    @Test
    fun `passes purchase command to repository and returns its result`() = runBlocking {
        val shopRepository = FakeShopRepository(
            initialState = ShopState(
                offers = emptyList(),
                lastAutomaticRefreshDate = TEST_DATE,
                manualRefreshCount = 0
            )
        )
        val expectedResult = PurchaseShopOfferResult.InsufficientSilver(
            requiredSilverAmount = 100,
            currentSilverAmount = 50
        )
        shopRepository.purchaseOfferResult = expectedResult

        val result = createUseCase(shopRepository)(TEST_ITEM_ID)

        assertEquals(expectedResult, result)
        assertEquals(TEST_PLAYER_ID, shopRepository.lastPurchasedPlayerId)
        assertEquals(TEST_ITEM_ID, shopRepository.lastPurchasedItemId)
        assertEquals(1, shopRepository.purchaseOfferCallCount)
        assertEquals(0, shopRepository.saveCallCount)
    }

    @Test
    fun `refreshes outdated shop state before purchase`() = runBlocking {
        val shopRepository = FakeShopRepository(
            initialState = ShopState(
                offers = emptyList(),
                lastAutomaticRefreshDate = TEST_DATE.minusDays(1),
                manualRefreshCount = 2
            )
        )

        createUseCase(shopRepository)(TEST_ITEM_ID)

        assertEquals(1, shopRepository.saveCallCount)
        assertEquals(TEST_DATE, shopRepository.state?.lastAutomaticRefreshDate)
        assertEquals(0, shopRepository.state?.manualRefreshCount)
        assertEquals(1, shopRepository.purchaseOfferCallCount)
    }

    private fun createUseCase(
        shopRepository: FakeShopRepository
    ): PurchaseShopOfferUseCase {
        val playerRepository = FakePlayerRepository()
        val getOrCreatePlayerStateUseCase = GetOrCreatePlayerStateUseCase(
            loadPlayerStateUseCase = LoadPlayerStateUseCase(playerRepository),
            savePlayerStateUseCase = SavePlayerStateUseCase(playerRepository)
        )

        return PurchaseShopOfferUseCase(
            getOrRefreshShopStateUseCase = GetOrRefreshShopStateUseCase(
                shopRepository = shopRepository,
                getOrCreatePlayerStateUseCase = getOrCreatePlayerStateUseCase,
                generateShopOffersUseCase = ShopTestFixtures.createGenerateShopOffersUseCase(),
                clock = ShopTestFixtures.createClock(TEST_DATE)
            ),
            getOrCreatePlayerStateUseCase = getOrCreatePlayerStateUseCase,
            shopRepository = shopRepository
        )
    }

    private companion object {
        const val TEST_PLAYER_ID = 1L
        const val TEST_ITEM_ID = 42L

        val TEST_DATE: LocalDate = LocalDate.of(2026, 8, 8)
    }
}