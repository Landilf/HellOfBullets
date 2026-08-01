package ru.landilf.hellofbullets.data.storage.mappers.equipment

import ru.landilf.hellofbullets.data.storage.entities.equipment.ArmorItemEntity
import ru.landilf.hellofbullets.data.storage.entities.equipment.ArtifactItemEntity
import ru.landilf.hellofbullets.data.storage.entities.equipment.WeaponItemEntity
import ru.landilf.hellofbullets.domain.model.equipment.ArmorItem
import ru.landilf.hellofbullets.domain.model.equipment.ArtifactItem
import ru.landilf.hellofbullets.domain.model.equipment.Item
import ru.landilf.hellofbullets.domain.model.equipment.WeaponItem
import javax.inject.Inject

data class EquipmentStorageData(
    val weaponItems: List<WeaponItemEntity>,
    val armorItems: List<ArmorItemEntity>,
    val artifactItems: List<ArtifactItemEntity>
)

class EquipmentStorageMapper @Inject constructor(
    private val weaponItemEntityToDomainMapper: WeaponItemEntityToDomainMapper,
    private val weaponItemDomainToEntityMapper: WeaponItemDomainToEntityMapper,
    private val armorItemEntityToDomainMapper: ArmorItemEntityToDomainMapper,
    private val armorItemDomainToEntityMapper: ArmorItemDomainToEntityMapper,
    private val artifactItemEntityToDomainMapper: ArtifactItemEntityToDomainMapper,
    private val artifactItemDomainToEntityMapper: ArtifactItemDomainToEntityMapper
) {
    fun toDomain(
        weaponEntities: List<WeaponItemEntity>,
        armorEntities: List<ArmorItemEntity>,
        artifactEntities: List<ArtifactItemEntity>
    ): List<Item> {
        return buildList {
            addAll(weaponEntities.map(weaponItemEntityToDomainMapper))
            addAll(armorEntities.map(armorItemEntityToDomainMapper))
            addAll(artifactEntities.map(artifactItemEntityToDomainMapper))
        }
    }

    fun toStorage(
        items: List<Item>,
        ownerId: Long
    ): EquipmentStorageData {
        return EquipmentStorageData(
            weaponItems = items
                .filterIsInstance<WeaponItem>()
                .map { weaponItemDomainToEntityMapper(it, ownerId) },
            armorItems = items
                .filterIsInstance<ArmorItem>()
                .map { armorItemDomainToEntityMapper(it, ownerId) },
            artifactItems = items
                .filterIsInstance<ArtifactItem>()
                .map { artifactItemDomainToEntityMapper(it, ownerId) }
        )
    }
}
