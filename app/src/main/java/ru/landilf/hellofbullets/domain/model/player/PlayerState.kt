package ru.landilf.hellofbullets.domain.model.player

import ru.landilf.hellofbullets.domain.model.equipment.ArmorItem
import ru.landilf.hellofbullets.domain.model.equipment.ArtifactItem
import ru.landilf.hellofbullets.domain.model.equipment.Item
import ru.landilf.hellofbullets.domain.model.equipment.WeaponItem

data class PlayerState(
    val playerProfile: PlayerProfile,
    val playerBuild: PlayerBuild,
    val inventory: Inventory
) {
    fun replaceItem(
        updatedItem: Item,
        removedItemIds: Set<Long> = emptySet()
    ): PlayerState {
        require(inventory.ownedItems.any { it.id == updatedItem.id }) {
            "Игрок не владеет предметом с id ${updatedItem.id}"
        }
        require(updatedItem.id !in removedItemIds) {
            "Обновляемый предмет не может быть удалён"
        }
        require(removedItemIds.all { removedItemId ->
            inventory.ownedItems.any { it.id == removedItemId }
        }) {
            "Игрок не владеет одним из удаляемых предметов"
        }

        val updatedBuild = playerBuild.copy(
            equippedWeaponItem = when (playerBuild.equippedWeaponItem?.id) {
                in removedItemIds -> null
                updatedItem.id ->
                    updatedItem as? WeaponItem
                        ?: error("Тип обновлённого оружия не совпадает")

                else -> playerBuild.equippedWeaponItem
            },
            equippedArmorItem = when (playerBuild.equippedArmorItem?.id) {
                in removedItemIds -> null
                updatedItem.id ->
                    updatedItem as? ArmorItem
                        ?: error("Тип обновлённой брони не совпадает")

                else -> playerBuild.equippedArmorItem
            },
            equippedArtifactItem = when (playerBuild.equippedArtifactItem?.id) {
                in removedItemIds -> null
                updatedItem.id ->
                    updatedItem as? ArtifactItem
                        ?: error("Тип обновлённого артефакта не совпадает")

                else -> playerBuild.equippedArtifactItem
            }
        )

        return copy(
            playerBuild = updatedBuild,
            inventory = inventory.copy(
                ownedItems = inventory.ownedItems
                    .filterNot { it.id in removedItemIds }
                    .map { item ->
                        if (item.id == updatedItem.id) {
                            updatedItem
                        } else {
                            item
                        }
                    }
            )
        )
    }
}
