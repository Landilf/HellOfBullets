package ru.landilf.hellofbullets.data.storage.repository

import ru.landilf.hellofbullets.data.storage.dao.PlayerDao
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
        val playerEntity = playerDao.getPlayerProfile() ?: return null
        val playerProfile = entityToDomainMapper(playerEntity)

        return PlayerState(
            playerProfile = playerProfile,
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

    override suspend fun savePlayerState(state: PlayerState) {
        playerDao.upsertPlayerProfile(domainToEntityMapper(state.playerProfile))
    }

    override suspend fun clearPlayerState() {
        playerDao.clearPlayerProfile()
    }

}