package ru.landilf.hellofbullets.domain.usecase.shop

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.landilf.hellofbullets.domain.engine.shop.ShopManualRefreshCostCalculator
import ru.landilf.hellofbullets.domain.model.player.Inventory
import ru.landilf.hellofbullets.domain.model.player.PlayerBuild
import ru.landilf.hellofbullets.domain.model.player.PlayerProfile
import ru.landilf.hellofbullets.domain.model.player.PlayerState
import ru.landilf.hellofbullets.domain.model.shop.ManualShopRefreshResult
import ru.landilf.hellofbullets.domain.model.shop.ShopState
import ru.landilf.hellofbullets.domain.usecase.FakePlayerRepository
import ru.landilf.hellofbullets.domain.usecase.FakeShopRepository
import ru.landilf.hellofbullets.domain.usecase.player.GetOrCreatePlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.player.LoadPlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.player.SavePlayerStateUseCase
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneOffset

class RefreshShopManuallyUseCaseTest {

    @Test
    fun `refreshes shop deducts silver and preserves automatic refresh date`() = runBlocking {
        val playerRepository = FakePlayerRepository(
            initialState = createPlayerState(
                silverAmount = 100
            )
        )
        val shopRepository = FakeShopRepository(
            initialState = createShopState(
                manualRefreshCount = 0
            )
        )
        val useCase = createUseCase(playerRepository, shopRepository)

        val result = useCase() as ManualShopRefreshResult.Success

        assertEquals(55, result.spentSilverAmount)
        assertEquals(45, result.remainingSilverAmount)
        assertEquals(TEST_DATE, result.shopState.lastAutomaticRefreshDate)
        assertEquals(1, result.shopState.manualRefreshCount)
        assertEquals(16, result.shopState.offers.size)
        assertEquals(TEST_PLAYER_ID, shopRepository.lastManualRefreshPlayerId)
        assertEquals(45, shopRepository.lastUpdatedSilverAmount)
        assertEquals(1, shopRepository.manualRefreshCallCount)
        assertEquals(0, shopRepository.saveCallCount)
    }

    @Test
    fun `uses next refresh cost based on completed refresh count`() = runBlocking {
        val playerRepository = FakePlayerRepository(
            initialState = createPlayerState(
                silverAmount = 100
            )
        )
        val shopRepository = FakeShopRepository(
            initialState = createShopState(
                manualRefreshCount = 1
            )
        )
        val useCase = createUseCase(playerRepository, shopRepository)

        val result = useCase() as ManualShopRefreshResult.Success

        assertEquals(75, result.spentSilverAmount)
        assertEquals(25, result.remainingSilverAmount)
        assertEquals(2, result.shopState.manualRefreshCount)
    }

    @Test
    fun `returns insufficient silver without changing shop state`() = runBlocking {
        val initialShopState = createShopState(manualRefreshCount = 0)
        val playerRepository = FakePlayerRepository(
            initialState = createPlayerState(
                silverAmount = 54
            )
        )
        val shopRepository = FakeShopRepository(
            initialState = initialShopState
        )
        val useCase = createUseCase(playerRepository, shopRepository)

        val result = useCase()

        assertEquals(
            ManualShopRefreshResult.InsufficientSilver(
                requiredSilverAmount = 55,
                currentSilverAmount = 54
            ),
            result
        )
        assertEquals(initialShopState, shopRepository.state)
        assertEquals(0, shopRepository.manualRefreshCallCount)
        assertEquals(0, shopRepository.saveCallCount)
    }

    @Test
    fun `returns daily limit without changing shop state`() = runBlocking {
        val initialShopState = createShopState(
            manualRefreshCount = ShopState.MAX_MANUAL_REFRESH_COUNT
        )
        val playerRepository = FakePlayerRepository(
            initialState = createPlayerState(
                silverAmount = 10_000
            )
        )
        val shopRepository = FakeShopRepository(
            initialState = initialShopState
        )
        val useCase = createUseCase(playerRepository, shopRepository)

        val result = useCase()

        assertEquals(ManualShopRefreshResult.DailyLimitReached, result)
        assertEquals(initialShopState, shopRepository.state)
        assertEquals(0, shopRepository.manualRefreshCallCount)
        assertEquals(0, shopRepository.saveCallCount)
    }

    private fun createUseCase(
        playerRepository: FakePlayerRepository,
        shopRepository: FakeShopRepository
    ): RefreshShopManuallyUseCase {
        val getOrCreatePlayerStateUseCase = GetOrCreatePlayerStateUseCase(
            loadPlayerStateUseCase = LoadPlayerStateUseCase(playerRepository),
            savePlayerStateUseCase = SavePlayerStateUseCase(playerRepository)
        )
        val generateShopOffersUseCase = ShopTestFixtures.createGenerateShopOffersUseCase()

        return RefreshShopManuallyUseCase(
            getOrRefreshShopStateUseCase = GetOrRefreshShopStateUseCase(
                shopRepository = shopRepository,
                getOrCreatePlayerStateUseCase = getOrCreatePlayerStateUseCase,
                generateShopOffersUseCase = generateShopOffersUseCase,
                clock = createClock()
            ),
            getOrCreatePlayerStateUseCase = getOrCreatePlayerStateUseCase,
            generateShopOffersUseCase = generateShopOffersUseCase,
            shopManualRefreshCostCalculator = ShopManualRefreshCostCalculator(),
            shopRepository = shopRepository
        )
    }

    private fun createPlayerState(
        silverAmount: Int
    ): PlayerState {
        return PlayerState(
            playerProfile = PlayerProfile(
                id = TEST_PLAYER_ID,
                name = "Player",
                level = 1,
                totalExperience = 0,
                silverAmount = silverAmount,
                skillPointAmount = 0
            ),
            playerBuild = PlayerBuild(
                equippedWeaponItem = null,
                equippedArmorItem = null,
                equippedArtifactItem = null,
                firstSkillSlot = null,
                secondSkillSlot = null
            ),
            inventory = Inventory(ownedItems = emptyList())
        )
    }

    private fun createShopState(
        manualRefreshCount: Int
    ): ShopState {
        return ShopState(
            offers = emptyList(),
            lastAutomaticRefreshDate = TEST_DATE,
            manualRefreshCount = manualRefreshCount
        )
    }

    private fun createClock(): Clock {
        return Clock.fixed(
            TEST_DATE.atStartOfDay(ZoneOffset.UTC).toInstant(),
            ZoneOffset.UTC
        )
    }

    private companion object {
        const val TEST_PLAYER_ID = 1L

        val TEST_DATE: LocalDate = LocalDate.of(2026, 8, 5)
    }
}