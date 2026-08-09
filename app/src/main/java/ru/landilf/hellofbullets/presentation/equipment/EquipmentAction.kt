package ru.landilf.hellofbullets.presentation.equipment

sealed interface EquipmentAction {
    data class OnItemClick(
        val itemId: Long
    ) : EquipmentAction

    data object OnItemDetailsDismiss : EquipmentAction

    data object OnToggleEquipmentClick : EquipmentAction
}