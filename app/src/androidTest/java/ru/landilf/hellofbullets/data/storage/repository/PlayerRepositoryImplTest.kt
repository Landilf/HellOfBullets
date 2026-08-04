package ru.landilf.hellofbullets.data.storage.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.landilf.hellofbullets.data.storage.database.AppDatabase
import ru.landilf.hellofbullets.data.storage.mappers.equipment.ArmorItemDomainToEntityMapper
import ru.landilf.hellofbullets.data.storage.mappers.equipment.ArmorItemEntityToDomainMapper
import ru.landilf.hellofbullets.data.storage.mappers.equipment.ArtifactItemDomainToEntityMapper
import ru.landilf.hellofbullets.data.storage.mappers.equipment.ArtifactItemEntityToDomainMapper
import ru.landilf.hellofbullets.data.storage.mappers.equipment.EquipmentStorageMapper
import ru.landilf.hellofbullets.data.storage.mappers.equipment.WeaponItemDomainToEntityMapper
import ru.landilf.hellofbullets.data.storage.mappers.equipment.WeaponItemEntityToDomainMapper
import ru.landilf.hellofbullets.data.storage.mappers.player.PlayerBuildDomainToEntityMapper
import ru.landilf.hellofbullets.data.storage.mappers.player.PlayerBuildEntityToDomainMapper
import ru.landilf.hellofbullets.data.storage.mappers.player.PlayerProfileDomainToEntityMapper
import ru.landilf.hellofbullets.data.storage.mappers.player.PlayerProfileEntityToDomainMapper
import ru.landilf.hellofbullets.data.storage.mappers.player.PlayerStateStorageMapper
import ru.landilf.hellofbullets.domain.model.equipment.ArmorItem
import ru.landilf.hellofbullets.domain.model.equipment.ArtifactItem
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQuality
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType
import ru.landilf.hellofbullets.domain.model.equipment.WeaponItem
import ru.landilf.hellofbullets.domain.model.player.Inventory
import ru.landilf.hellofbullets.domain.model.player.PlayerBuild
import ru.landilf.hellofbullets.domain.model.player.PlayerProfile
import ru.landilf.hellofbullets.domain.model.player.PlayerState

@RunWith(AndroidJUnit4::class)
class PlayerRepositoryImplTest {
    private lateinit var database: AppDatabase
    private lateinit var repository: PlayerRepositoryImpl

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        repository = PlayerRepositoryImpl(
            database = database,
            playerDao = database.playerDao(),
            equipmentDao = database.equipmentDao(),
            playerStateStorageMapper = createPlayerStateStorageMapper()
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun savesAndLoadsPlayerStateWithInventoryAndBuild() = runBlocking {
        val expectedState = createPlayerState()

        repository.savePlayerState(expectedState)

        assertEquals(expectedState, repository.getPlayerState())
    }

    @Test
    fun replacesPreviousInventoryAndBuildWhenSavingNewState() = runBlocking {
        repository.savePlayerState(createPlayerState())

        val updatedWeapon = WeaponItem(
            id = 4L,
            definitionId = 40L,
            level = 1,
            quality = EquipmentQuality.NORMAL,
            additionalStatType = EquipmentStatType.DAMAGE,
            additionalStatValue = 3f,
            damage = 10f,
            attackSpeed = 1.5f,
            specializationCoef = 0f
        )
        val updatedState = PlayerState(
            playerProfile = PlayerProfile(
                id = 1L,
                name = "Player",
                level = 6,
                totalExperience = 320,
                silverAmount = 150,
                skillPointAmount = 5
            ),
            playerBuild = PlayerBuild(
                equippedWeaponItem = updatedWeapon,
                equippedArmorItem = null,
                equippedArtifactItem = null,
                firstSkillSlot = null,
                secondSkillSlot = null
            ),
            inventory = Inventory(
                ownedItems = listOf(updatedWeapon)
            )
        )

        repository.savePlayerState(updatedState)

        assertEquals(updatedState, repository.getPlayerState())
    }

    @Test
    fun clearsProfileBuildAndInventory() = runBlocking {
        val state = createPlayerState()
        repository.savePlayerState(state)

        repository.clearPlayerState()

        assertEquals(null, repository.getPlayerState())
        assertTrue(
            database.equipmentDao().getWeaponItems(state.playerProfile.id).isEmpty()
        )
        assertTrue(
            database.equipmentDao().getArmorItems(state.playerProfile.id).isEmpty()
        )
        assertTrue(
            database.equipmentDao().getArtifactItems(state.playerProfile.id).isEmpty()
        )
        assertEquals(
            null,
            database.playerDao().getPlayerBuild(state.playerProfile.id)
        )
    }

    @Test
    fun observesSavedPlayerState() = runBlocking {
        val expectedState = createPlayerState()
        repository.savePlayerState(expectedState)

        val observedState = repository.observePlayerState().first()

        assertEquals(expectedState, observedState)
    }

    @Test
    fun emitsEmptyAndThenSavedPlayerState() = runBlocking {
        val expectedState = createPlayerState()
        val emptyStateObserved = CompletableDeferred<Unit>()

        val observedStates = async {
            repository.observePlayerState()
                .onEach { state ->
                    if (state == null) {
                        emptyStateObserved.complete(Unit)
                    }
                }
                .take(2)
                .toList()
        }

        withTimeout(5_000) {
            emptyStateObserved.await()
        }

        repository.savePlayerState(expectedState)

        assertEquals(
            listOf(null, expectedState),
            withTimeout(5_000) {
                observedStates.await()
            }
        )
    }

    private fun createPlayerStateStorageMapper(): PlayerStateStorageMapper {
        return PlayerStateStorageMapper(
            playerProfileEntityToDomainMapper = PlayerProfileEntityToDomainMapper(),
            playerProfileDomainToEntityMapper = PlayerProfileDomainToEntityMapper(),
            playerBuildEntityToDomainMapper = PlayerBuildEntityToDomainMapper(),
            playerBuildDomainToEntityMapper = PlayerBuildDomainToEntityMapper(),
            equipmentStorageMapper = EquipmentStorageMapper(
                weaponItemEntityToDomainMapper = WeaponItemEntityToDomainMapper(),
                weaponItemDomainToEntityMapper = WeaponItemDomainToEntityMapper(),
                armorItemEntityToDomainMapper = ArmorItemEntityToDomainMapper(),
                armorItemDomainToEntityMapper = ArmorItemDomainToEntityMapper(),
                artifactItemEntityToDomainMapper = ArtifactItemEntityToDomainMapper(),
                artifactItemDomainToEntityMapper = ArtifactItemDomainToEntityMapper()
            )
        )
    }

    private fun createPlayerState(
        specializationCoef: Float = 0f
    ): PlayerState {
        val weapon = WeaponItem(
            id = 1L,
            definitionId = 10L,
            level = 4,
            quality = EquipmentQuality.NORMAL,
            additionalStatType = EquipmentStatType.HP,
            additionalStatValue = 12f,
            damage = 18f,
            attackSpeed = 2.5f,
            specializationCoef = specializationCoef
        )
        val armor = ArmorItem(
            id = 2L,
            definitionId = 20L,
            level = 3,
            quality = EquipmentQuality.FINE,
            additionalStatType = EquipmentStatType.DAMAGE,
            additionalStatValue = 4f,
            hp = 80f,
            defense = 15f,
            specializationCoef = specializationCoef
        )
        val artifact = ArtifactItem(
            id = 3L,
            definitionId = 30L,
            level = 2,
            quality = EquipmentQuality.NORMAL,
            additionalStatType = EquipmentStatType.DURATION,
            additionalStatValue = 5f,
            cooldownReductionPercent = 10f,
            durationBonusPercent = 20f,
            specializationCoef = specializationCoef
        )

        return PlayerState(
            playerProfile = PlayerProfile(
                id = 1L,
                name = "Player",
                level = 5,
                totalExperience = 250,
                silverAmount = 100,
                skillPointAmount = 4
            ),
            playerBuild = PlayerBuild(
                equippedWeaponItem = weapon,
                equippedArmorItem = armor,
                equippedArtifactItem = artifact,
                firstSkillSlot = null,
                secondSkillSlot = null
            ),
            inventory = Inventory(listOf(weapon, armor, artifact))
        )
    }
}