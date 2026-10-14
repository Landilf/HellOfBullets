package ru.landilf.hellofbullets.data.storage.mappers.player

import ru.landilf.hellofbullets.data.storage.entities.equipment.ArmorItemEntity
import ru.landilf.hellofbullets.data.storage.entities.equipment.ArtifactItemEntity
import ru.landilf.hellofbullets.data.storage.entities.equipment.WeaponItemEntity
import ru.landilf.hellofbullets.data.storage.entities.player.PlayerBuildEntity
import ru.landilf.hellofbullets.data.storage.entities.player.PlayerProfileEntity
import ru.landilf.hellofbullets.data.storage.mappers.equipment.EquipmentStorageData
import ru.landilf.hellofbullets.data.storage.mappers.equipment.EquipmentStorageMapper
import ru.landilf.hellofbullets.domain.model.player.Inventory
import ru.landilf.hellofbullets.domain.model.player.PlayerState
import javax.inject.Inject

class PlayerStateStorageMapper @Inject constructor(
    private val playerProfileEntityToDomainMapper: PlayerProfileEntityToDomainMapper,
    private val playerProfileDomainToEntityMapper: PlayerProfileDomainToEntityMapper,
    private val playerBuildEntityToDomainMapper: PlayerBuildEntityToDomainMapper,
    private val playerBuildDomainToEntityMapper: PlayerBuildDomainToEntityMapper,
    private val equipmentStorageMapper: EquipmentStorageMapper
) {
    fun toDomain(
        profileEntity: PlayerProfileEntity,
        buildEntity: PlayerBuildEntity?,
        weaponEntities: List<WeaponItemEntity>,
        armorEntities: List<ArmorItemEntity>,
        artifactEntities: List<ArtifactItemEntity>
    ): PlayerState {
        val items = equipmentStorageMapper.toDomain(
            weaponEntities = weaponEntities,
            armorEntities = armorEntities,
            artifactEntities = artifactEntities
        )

        return PlayerState(
            playerProfile = playerProfileEntityToDomainMapper(profileEntity),
            playerBuild = playerBuildEntityToDomainMapper(
                entity = buildEntity,
                items = items
            ),
            inventory = Inventory(ownedItems = items)
        )
    }

    fun toProfileEntity(state: PlayerState): PlayerProfileEntity {
        return playerProfileDomainToEntityMapper(state.playerProfile)
    }

    fun toBuildEntity(
        state: PlayerState,
        playerId: Long
    ): PlayerBuildEntity {
        return playerBuildDomainToEntityMapper(
            build = state.playerBuild,
            playerId = playerId
        )
    }

    fun toEquipmentStorageData(
        state: PlayerState,
        ownerId: Long
    ): EquipmentStorageData {
        return equipmentStorageMapper.toStorage(
            items = state.inventory.ownedItems,
            ownerId = ownerId
        )
    }
}