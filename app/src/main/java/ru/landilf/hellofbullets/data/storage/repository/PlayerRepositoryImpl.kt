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
import ru.landilf.hellofbullets.domain.model.player.PlayerStateUpdate
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
            getPlayerStateInTransaction()
        }
    }

    override suspend fun savePlayerState(state: PlayerState) {
        database.withTransaction { savePlayerStateInTransaction(state) }
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

    override suspend fun <T> updatePlayerState(
        transform: (PlayerState) -> PlayerStateUpdate<T>
    ): T {
        return database.withTransaction {
            val playerState = requireNotNull(getPlayerStateInTransaction()) {
                "Состояние игрока не найдено"
            }
            val update = transform(playerState)

            savePlayerStateInTransaction(update.updatedState)

            update.result
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

    private suspend fun getPlayerStateInTransaction(): PlayerState? {
        val profileEntity = playerDao.getPlayerProfile() ?: return null
        val ownerId = profileEntity.id

        return playerStateStorageMapper.toDomain(
            profileEntity = profileEntity,
            buildEntity = playerDao.getPlayerBuild(ownerId),
            weaponEntities = equipmentDao.getWeaponItems(ownerId),
            armorEntities = equipmentDao.getArmorItems(ownerId),
            artifactEntities = equipmentDao.getArtifactItems(ownerId)
        )
    }

    private suspend fun savePlayerStateInTransaction(state: PlayerState) {
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