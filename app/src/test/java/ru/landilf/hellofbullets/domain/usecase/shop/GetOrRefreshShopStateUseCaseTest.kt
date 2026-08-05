package ru.landilf.hellofbullets.domain.usecase.shop

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.landilf.hellofbullets.domain.model.shop.ShopState
import ru.landilf.hellofbullets.domain.repository.ShopRepository
import ru.landilf.hellofbullets.domain.usecase.FakePlayerRepository
import ru.landilf.hellofbullets.domain.usecase.FakeShopRepository
import ru.landilf.hellofbullets.domain.usecase.player.GetOrCreatePlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.player.LoadPlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.player.SavePlayerStateUseCase
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneOffset


class GetOrRefreshShopStateUseCaseTest {

    @Test
    fun `creates and saves shop state when it does not exist`() = runBlocking {
        val shopRepository = FakeShopRepository()
        val useCase = createUseCase(shopRepository)

        val result = useCase()

        assertEquals(TEST_DATE, result.lastAutomaticRefreshDate)
        assertEquals(16, result.offers.size)
        assertEquals(result, shopRepository.state)
        assertEquals(1, shopRepository.saveCallCount)
    }

    @Test
    fun `returns saved shop state without refresh on the same day`() = runBlocking {
        val savedState = ShopState(
            offers = emptyList(),
            lastAutomaticRefreshDate = TEST_DATE,
            manualRefreshCount = 0
        )
        val shopRepository = FakeShopRepository(savedState)
        val useCase = createUseCase(shopRepository)

        val result = useCase()

        assertEquals(savedState, result)
        assertEquals(0, shopRepository.saveCallCount)
    }

    @Test
    fun `replaces saved shop state after date changes`() = runBlocking {
        val shopRepository = FakeShopRepository(
            ShopState(
                offers = emptyList(),
                lastAutomaticRefreshDate = TEST_DATE.minusDays(1),
                manualRefreshCount = 2
            )
        )
        val useCase = createUseCase(shopRepository)

        val result = useCase()

        assertEquals(0, result.manualRefreshCount)
        assertEquals(TEST_DATE, result.lastAutomaticRefreshDate)
        assertEquals(16, result.offers.size)
        assertEquals(result, shopRepository.state)
        assertEquals(1, shopRepository.saveCallCount)
    }

    private fun createUseCase(
        shopRepository: ShopRepository
    ): GetOrRefreshShopStateUseCase {
        val playerRepository = FakePlayerRepository()

        return GetOrRefreshShopStateUseCase(
            shopRepository = shopRepository,
            getOrCreatePlayerStateUseCase = GetOrCreatePlayerStateUseCase(
                loadPlayerStateUseCase = LoadPlayerStateUseCase(
                    playerRepository = playerRepository
                ),
                savePlayerStateUseCase = SavePlayerStateUseCase(
                    playerRepository = playerRepository
                )
            ),
            generateShopOffersUseCase = ShopTestFixtures.createGenerateShopOffersUseCase(),
            clock = createClock()
        )
    }

    private fun createClock(): Clock {
        return Clock.fixed(
            TEST_DATE.atStartOfDay(ZoneOffset.UTC).toInstant(),
            ZoneOffset.UTC
        )
    }

    private companion object {
        private val TEST_DATE = LocalDate.of(2026, 8, 5)
    }
}