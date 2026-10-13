package ru.landilf.hellofbullets.domain.usecase.equipment

import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQualityUpgradeOptions
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQualityUpgradeStatPreview
import ru.landilf.hellofbullets.domain.model.equipment.upgrade.EquipmentStatSource
import ru.landilf.hellofbullets.domain.model.equipment.upgrade.statValue
import ru.landilf.hellofbullets.domain.repository.EquipmentDefinitionRepository
import ru.landilf.hellofbullets.domain.usecase.player.GetOrCreatePlayerStateUseCase
import javax.inject.Inject

class GetEquipmentQualityUpgradeOptionsUseCase @Inject constructor(
    private val getOrCreatePlayerStateUseCase: GetOrCreatePlayerStateUseCase,
    private val equipmentDefinitionRepository: EquipmentDefinitionRepository
) {
    suspend operator fun invoke(
        itemId: Long
    ): EquipmentQualityUpgradeOptions {
        val playerState = getOrCreatePlayerStateUseCase()
        val item = requireNotNull(
            playerState.inventory.ownedItems.firstOrNull
            { it.id == itemId }
        ) {
            "Предмет с id $itemId не найден в инвентаре"
        }

        val nextQuality = item.quality.nextQuality
            ?: return EquipmentQualityUpgradeOptions.Unavailable(
                currentQuality = item.quality
            )

        val requiredMaterialsCount = checkNotNull(
            item.quality.materialsRequiredForUpgrade
        ) {
            "Не найдено количество материалов для повышения качества"
        }

        val definition = checkNotNull(
            equipmentDefinitionRepository.getDefinitionById(item.definitionId)
        ) {
            "Не найдено определение предмета с id ${item.definitionId}"
        }

        val primaryFirstStatValue = item.statValue(EquipmentStatSource.PRIMARY_FIRST)
        val qualityMultiplier = checkNotNull(item.quality.primaryFirstStatUpgradeMultiplier) {
            "Не найден множитель повышения качества"
        }

        val equippedItemIds = setOfNotNull(
            playerState.playerBuild.equippedWeaponItem?.id,
            playerState.playerBuild.equippedArmorItem?.id,
            playerState.playerBuild.equippedArtifactItem?.id
        )

        val eligibleMaterialItemIds = playerState.inventory.ownedItems
            .asSequence()
            .filter { material ->
                material.id !in equippedItemIds &&
                        material.id != item.id &&
                        material::class == item::class &&
                        material.definitionId == item.definitionId &&
                        material.quality == item.quality
            }
            .map { material -> material.id }
            .toList()

        return EquipmentQualityUpgradeOptions.Available(
            itemId = item.id,
            currentQuality = item.quality,
            nextQuality = nextQuality,
            primaryFirstStatPreview = EquipmentQualityUpgradeStatPreview(
                statType = definition.primaryFirstStatType,
                currentValue = primaryFirstStatValue,
                upgradedValue = primaryFirstStatValue * qualityMultiplier
            ),
            requiredMaterialsCount = requiredMaterialsCount,
            eligibleMaterialItemIds = eligibleMaterialItemIds
        )
    }
}