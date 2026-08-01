package ru.landilf.hellofbullets.data.storage.repository

import androidx.room.withTransaction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import ru.landilf.hellofbullets.data.storage.dao.EquipmentDao
import ru.landilf.hellofbullets.data.storage.dao.PlayerDao
import ru.landilf.hellofbullets.data.storage.database.AppDatabase
import ru.landilf.hellofbullets.data.storage.entities.player.PlayerProfileEntity
import ru.landilf.hellofbullets.data.storage.mappers.player.PlayerStateStorageMapper
import ru.landilf.hellofbullets.domain.model.player.PlayerState
import ru.landilf.hellofbullets.domain.repository.PlayerRepository
import javax.inject.Inject

class PlayerRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val playerDao: PlayerDao,
    private val equipmentDao: EquipmentDao,
    private val playerStateStorageMapper: PlayerStateStorageMapper
) : PlayerRepository {
    override suspend fun getPlayerState(): PlayerState? {
        return database.withTransaction {
            val profileEntity = playerDao.getPlayerProfile()
                ?: return@withTransaction null

            val ownerId = profileEntity.id

            playerStateStorageMapper.toDomain(
                profileEntity = profileEntity,
                buildEntity = playerDao.getPlayerBuild(ownerId),
                weaponEntities = equipmentDao.getWeaponItems(ownerId),
                armorEntities = equipmentDao.getArmorItems(ownerId),
                artifactEntities = equipmentDao.getArtifactItems(ownerId)
            )
        }
    }

    override suspend fun savePlayerState(state: PlayerState) {
        database.withTransaction {
            val ownerId = state.playerProfile.id
            val equipmentData = playerStateStorageMapper.toEquipmentStorageData(
                state = state,
                ownerId = ownerId
            )

            playerDao.upsertPlayerProfile(
                playerStateStorageMapper.toProfileEntity(state)
            )
            equipmentDao.replaceEquipment(
                ownerId = ownerId,
                weaponItems = equipmentData.weaponItems,
                armorItems = equipmentData.armorItems,
                artifactItems = equipmentData.artifactItems

            )
            playerDao.upsertPlayerBuild(
                playerStateStorageMapper.toBuildEntity(
                    state = state,
                    playerId = ownerId
                )
            )
        }
    }

    override suspend fun clearPlayerState() {
        playerDao.clearPlayerProfile()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observePlayerState(): Flow<PlayerState?> {
        return playerDao.observePlayerProfile()
            .flatMapLatest { profileEntity ->
                if (profileEntity == null) {
                    flowOf(null)
                } else {
                    observePlayerState(profileEntity)
                }
            }
    }

    private fun observePlayerState(
        profileEntity: PlayerProfileEntity
    ): Flow<PlayerState> {
        val ownerId = profileEntity.id

        return combine(
            playerDao.observePlayerBuild(ownerId),
            equipmentDao.observeWeaponItems(ownerId),
            equipmentDao.observeArmorItems(ownerId),
            equipmentDao.observeArtifactItems(ownerId),
        ) { buildEntity, weaponEntities, armorEntities, artifactEntities ->
            playerStateStorageMapper.toDomain(
                profileEntity = profileEntity,
                buildEntity = buildEntity,
                weaponEntities = weaponEntities,
                armorEntities = armorEntities,
                artifactEntities = artifactEntities
            )
        }
    }
}