package ru.landilf.hellofbullets.presentation.equipment

data class EquipmentUiState(
    val isLoading: Boolean = true,
    val weaponItems: List<EquipmentItemUiModel> = emptyList(),
    val armorItems: List<EquipmentItemUiModel> = emptyList(),
    val artifactItems: List<EquipmentItemUiModel> = emptyList(),
    val selectedItemId: Long? = null,
    val isItemDetailsVisible: Boolean = false,
    val equippedWeaponItemId: Long? = null,
    val equippedArmorItemId: Long? = null,
    val equippedArtifactItemId: Long? = null,
    val errorMessage: String? = null
) {
    fun selectedItem(): EquipmentItemUiModel? {
        val selectedItemId = selectedItemId ?: return null

        return (weaponItems + armorItems + artifactItems)
            .firstOrNull { item -> item.itemId == selectedItemId }
    }
}