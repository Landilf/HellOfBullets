package ru.landilf.hellofbullets.data.storage.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.landilf.hellofbullets.data.storage.dao.PlayerDao
import ru.landilf.hellofbullets.data.storage.entities.player.PlayerProfileEntity
import ru.landilf.hellofbullets.data.storage.mappers.PlayerProfileDomainToEntityMapper
import ru.landilf.hellofbullets.data.storage.mappers.PlayerProfileEntityToDomainMapper
import ru.landilf.hellofbullets.domain.model.player.Inventory
import ru.landilf.hellofbullets.domain.model.player.PlayerBuild
import ru.landilf.hellofbullets.domain.model.player.PlayerState
import ru.landilf.hellofbullets.domain.repository.PlayerRepository
import javax.inject.Inject

class PlayerRepositoryImpl @Inject constructor(
    private val playerDao: PlayerDao,
    private val entityToDomainMapper: PlayerProfileEntityToDomainMapper,
    private val domainToEntityMapper: PlayerProfileDomainToEntityMapper
) : PlayerRepository {
    override suspend fun getPlayerState(): PlayerState? {
        return playerDao.getPlayerProfile()?.let(::createPlayerState)
    }

    override suspend fun savePlayerState(state: PlayerState) {
        playerDao.upsertPlayerProfile(domainToEntityMapper(state.playerProfile))
    }

    override suspend fun clearPlayerState() {
        playerDao.clearPlayerProfile()
    }

    override fun observePlayerState(): Flow<PlayerState?> {
        return playerDao.observePlayerProfile().map { entity ->
            entity?.let(::createPlayerState)
        }
    }

    private fun createPlayerState(
        profileEntity: PlayerProfileEntity
    ): PlayerState {
        return PlayerState(
            playerProfile = entityToDomainMapper(profileEntity),
            playerBuild = PlayerBuild(
                equippedWeaponItem = null,
                equippedArmorItem = null,
                equippedArtifactItem = null,
                firstSkillSlot = null,
                secondSkillSlot = null
            ),
            inventory = Inventory(
                ownedItems = emptyList()
            )
        )
    }
}