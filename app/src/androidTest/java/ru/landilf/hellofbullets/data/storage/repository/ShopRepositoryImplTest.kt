package ru.landilf.hellofbullets.data.storage.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import ru.landilf.hellofbullets.data.storage.database.AppDatabase
import ru.landilf.hellofbullets.data.storage.entities.player.PlayerProfileEntity
import ru.landilf.hellofbullets.data.storage.mappers.shop.ShopArmorOfferDomainToEntityMapper
import ru.landilf.hellofbullets.data.storage.mappers.shop.ShopArmorOfferEntityToDomainMapper
import ru.landilf.hellofbullets.data.storage.mappers.shop.ShopArtifactOfferDomainToEntityMapper
import ru.landilf.hellofbullets.data.storage.mappers.shop.ShopArtifactOfferEntityToDomainMapper
import ru.landilf.hellofbullets.data.storage.mappers.shop.ShopStateDomainToEntityMapper
import ru.landilf.hellofbullets.data.storage.mappers.shop.ShopStateEntityToDomainMapper
import ru.landilf.hellofbullets.data.storage.mappers.shop.ShopStorageMapper
import ru.landilf.hellofbullets.data.storage.mappers.shop.ShopWeaponOfferDomainToEntityMapper
import ru.landilf.hellofbullets.data.storage.mappers.shop.ShopWeaponOfferEntityToDomainMapper
import ru.landilf.hellofbullets.domain.model.equipment.ArmorItem
import ru.landilf.hellofbullets.domain.model.equipment.ArtifactItem
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQuality
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType
import ru.landilf.hellofbullets.domain.model.equipment.WeaponItem
import ru.landilf.hellofbullets.domain.model.shop.ShopOffer
import ru.landilf.hellofbullets.domain.model.shop.ShopState
import java.time.LocalDate

class ShopRepositoryImplTest {
    private lateinit var database: AppDatabase
    private lateinit var repository: ShopRepositoryImpl

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        repository = ShopRepositoryImpl(
            database = database,
            shopDao = database.shopDao(),
            shopStorageMapper = createShopStorageMapper(),
            playerDao = database.playerDao()
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun savesAndLoadsShopStateWithMixedOffersInOriginalOrder() = runBlocking {
        val expectedState = createShopState()

        repository.saveShopState(expectedState)

        assertEquals(expectedState, repository.getShopState())
    }

    @Test
    fun replacesPreviousOfferWhenSavingNewShopState() = runBlocking {
        repository.saveShopState(createShopState())

        val updatedState = ShopState(
            offers = listOf(
                ShopOffer(
                    item = WeaponItem(
                        id = 10L,
                        definitionId = 10L,
                        level = 1,
                        quality = EquipmentQuality.FINE,
                        additionalStatType = EquipmentStatType.DAMAGE,
                        additionalStatValue = 5f,
                        damage = 12f,
                        attackSpeed = 2f,
                        specializationCoef = 0.5f
                    ),
                    purchasePrice = 200
                )
            ),
            lastAutomaticRefreshDate = LocalDate.of(2026, 8, 6),
            manualRefreshCount = 1
        )

        repository.saveShopState(updatedState)

        assertEquals(updatedState, repository.getShopState())
    }

    @Test
    fun appliesManualRefreshAndUpdatesPlayerSilverWithShopState() = runBlocking {
        val playerId = 1L
        val initialShopState = createShopState()
        val refreshedShopState = initialShopState.copy(
            lastAutomaticRefreshDate = LocalDate.of(2026, 8, 6),
            manualRefreshCount = 3
        )

        database.playerDao().upsertPlayerProfile(
            PlayerProfileEntity(
                id = playerId,
                name = "Player",
                level = 1,
                totalExperience = 0,
                silverAmount = 100,
                skillPointAmount = 0,
            )
        )
        repository.saveShopState(initialShopState)

        repository.applyManualRefresh(
            playerId = playerId,
            updatedSilverAmount = 45,
            refreshedShopState = refreshedShopState
        )
        assertEquals(
            45,
            database.playerDao().getPlayerProfile()?.silverAmount
        )
        assertEquals(refreshedShopState, repository.getShopState())
    }

    private fun createShopStorageMapper(): ShopStorageMapper {
        return ShopStorageMapper(
            shopStateEntityToDomainMapper = ShopStateEntityToDomainMapper(),
            shopStateDomainToEntityMapper = ShopStateDomainToEntityMapper(),
            shopWeaponOfferEntityToDomainMapper = ShopWeaponOfferEntityToDomainMapper(),
            shopWeaponOfferDomainToEntityMapper = ShopWeaponOfferDomainToEntityMapper(),
            shopArmorOfferEntityToDomainMapper = ShopArmorOfferEntityToDomainMapper(),
            shopArmorOfferDomainToEntityMapper = ShopArmorOfferDomainToEntityMapper(),
            shopArtifactOfferEntityToDomainMapper = ShopArtifactOfferEntityToDomainMapper(),
            shopArtifactOfferDomainToEntityMapper = ShopArtifactOfferDomainToEntityMapper()
        )
    }

    private fun createShopState(): ShopState {
        val artifactOffer = ShopOffer(
            item = ArtifactItem(
                id = 3L,
                definitionId = 30L,
                level = 1,
                quality = EquipmentQuality.NORMAL,
                additionalStatType = EquipmentStatType.HP,
                additionalStatValue = 15f,
                cooldownReductionPercent = 5f,
                durationBonusPercent = 6f,
                specializationCoef = -0.5f
            ),
            purchasePrice = 120
        )
        val weaponOffer = ShopOffer(
            item = WeaponItem(
                id = 1L,
                definitionId = 10L,
                level = 1,
                quality = EquipmentQuality.FINE,
                additionalStatType = EquipmentStatType.DEFENSE,
                additionalStatValue = 2f,
                damage = 11f,
                attackSpeed = 2f,
                specializationCoef = 0.5f
            ),
            purchasePrice = 200
        )
        val armorOffer = ShopOffer(
            item = ArmorItem(
                id = 2L,
                definitionId = 20L,
                level = 1,
                quality = EquipmentQuality.SUPERIOR,
                additionalStatType = EquipmentStatType.DURATION,
                additionalStatValue = 3f,
                hp = 100f,
                defense = 5f,
                specializationCoef = 0f
            ),
            purchasePrice = 600
        )

        return ShopState(
            offers = listOf(weaponOffer, armorOffer, artifactOffer),
            lastAutomaticRefreshDate = LocalDate.of(2026, 8, 5),
            manualRefreshCount = 2
        )
    }
}