package ru.landilf.hellofbullets.presentation.equipment

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.material.icons.outlined.Shield
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.landilf.hellofbullets.domain.model.equipment.ArmorItem
import ru.landilf.hellofbullets.domain.model.equipment.ArtifactItem
import ru.landilf.hellofbullets.domain.model.equipment.Item
import ru.landilf.hellofbullets.domain.model.equipment.WeaponItem
import ru.landilf.hellofbullets.domain.model.equipment.definition.EquipmentDefinition
import ru.landilf.hellofbullets.domain.model.equipment.upgrade.EquipmentLevelUpgradeOptions
import ru.landilf.hellofbullets.domain.model.player.EquipmentSlot
import ru.landilf.hellofbullets.domain.model.player.PlayerState
import ru.landilf.hellofbullets.domain.usecase.equipment.GetEquipmentDefinitionByIdUseCase
import ru.landilf.hellofbullets.domain.usecase.equipment.GetEquipmentLevelUpgradeOptionsUseCase
import ru.landilf.hellofbullets.domain.usecase.equipment.SetEquippedItemUseCase
import ru.landilf.hellofbullets.domain.usecase.equipment.UpgradePlayerEquipmentLevelsUseCase
import ru.landilf.hellofbullets.domain.usecase.player.GetOrCreatePlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.player.ObservePlayerStateUseCase
import ru.landilf.hellofbullets.presentation.common.equipment.EquipmentStatUiModel
import javax.inject.Inject


@HiltViewModel
class EquipmentViewModel @Inject constructor(
    private val getOrCreatePlayerStateUseCase: GetOrCreatePlayerStateUseCase,
    private val observePlayerStateUseCase: ObservePlayerStateUseCase,
    private val getEquipmentDefinitionByIdUseCase: GetEquipmentDefinitionByIdUseCase,
    private val setEquippedItemUseCase: SetEquippedItemUseCase,
    private val getEquipmentLevelUpgradeOptionsUseCase: GetEquipmentLevelUpgradeOptionsUseCase,
    private val upgradePlayerEquipmentLevelsUseCase: UpgradePlayerEquipmentLevelsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(EquipmentUiState())
    val uiState: StateFlow<EquipmentUiState> = _uiState.asStateFlow()

    init {
        observePlayerState()
        createPlayerStateIfNeeded()
    }

    fun onAction(action: EquipmentAction) {
        when (action) {
            is EquipmentAction.OnItemClick -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        selectedItemId = action.itemId,
                        isItemDetailsVisible = true,
                        errorMessage = null
                    )
                }
            }

            EquipmentAction.OnItemDetailsDismiss -> {
                _uiState.update { currentState ->
                    currentState.copy(isItemDetailsVisible = false)
                }
            }

            EquipmentAction.OnToggleEquipmentClick -> {
                toggleSelectedItemEquipment()
            }

            EquipmentAction.OnLevelUpgradeClick -> {
                openLevelUpgradeOverlay()
            }

            EquipmentAction.OnLevelUpgradeConfirmClick -> {
                confirmLevelUpgrade()
            }

            EquipmentAction.OnLevelUpgradeLevelsDecrease -> {
                decreaseSelectedUpgradeLevels()
            }

            EquipmentAction.OnLevelUpgradeLevelsIncrease -> {
                increaseSelectedUpgradeLevels()
            }

            EquipmentAction.OnLevelUpgradeOverlayDismiss -> {
                _uiState.update { state ->
                    state.copy(levelUpgradeOverlay = null)
                }
            }
        }
    }

    private fun observePlayerState() {
        viewModelScope.launch {
            observePlayerStateUseCase().collect { playerState ->
                if (playerState != null) {
                    updateUiState(playerState)
                }
            }
        }
    }

    private fun updateUiState(playerState: PlayerState) {
        val equippedItemIds = setOfNotNull(
            playerState.playerBuild.equippedWeaponItem?.id,
            playerState.playerBuild.equippedArmorItem?.id,
            playerState.playerBuild.equippedArtifactItem?.id
        )

        val weaponItems = playerState.inventory.ownedItems
            .filterIsInstance<WeaponItem>()
            .map { item -> item.toUiModel(item.id in equippedItemIds) }

        val armorItems = playerState.inventory.ownedItems
            .filterIsInstance<ArmorItem>()
            .map { item -> item.toUiModel(item.id in equippedItemIds) }

        val artifactItems = playerState.inventory.ownedItems
            .filterIsInstance<ArtifactItem>()
            .map { item -> item.toUiModel(item.id in equippedItemIds) }

        val ownedItemIds = playerState.inventory.ownedItems.map { it.id }.toSet()

        _uiState.update { currentState ->
            val selectedItemId = currentState.selectedItemId?.takeIf { it in ownedItemIds }

            currentState.copy(
                isLoading = false,
                weaponItems = weaponItems,
                armorItems = armorItems,
                artifactItems = artifactItems,
                selectedItemId = selectedItemId,
                isItemDetailsVisible = currentState.isItemDetailsVisible && selectedItemId != null,
                equippedWeaponItemId = playerState.playerBuild.equippedWeaponItem?.id,
                equippedArmorItemId = playerState.playerBuild.equippedArmorItem?.id,
                equippedArtifactItemId = playerState.playerBuild.equippedArtifactItem?.id,
                errorMessage = null
            )
        }
    }

    private fun createPlayerStateIfNeeded() {
        viewModelScope.launch {
            try {
                getOrCreatePlayerStateUseCase()
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        errorMessage = exception.message
                            ?: "Не удалось загрузить снаряжение"
                    )
                }
            }
        }
    }

    private fun toggleSelectedItemEquipment() {
        val selectedItem = _uiState.value.selectedItem() ?: return

        viewModelScope.launch {
            try {
                setEquippedItemUseCase(
                    slot = selectedItem.slot,
                    itemId = if (selectedItem.isEquipped) {
                        null
                    } else {
                        selectedItem.itemId
                    }
                )

                _uiState.update { currentState ->
                    currentState.copy(
                        errorMessage = null
                    )
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        errorMessage = exception.message
                            ?: "Не удалось изменить экипировку"
                    )
                }
            }
        }
    }

    private fun openLevelUpgradeOverlay() {
        if (_uiState.value.isLevelUpgradeInProgress) return

        val itemId = _uiState.value.selectedItemId ?: return

        viewModelScope.launch {
            try {
                _uiState.update { state ->
                    state.copy(
                        isLevelUpgradeInProgress = true,
                        errorMessage = null
                    )
                }

                val options = getEquipmentLevelUpgradeOptionsUseCase(itemId)

                _uiState.update { state ->
                    state.copy(
                        levelUpgradeOverlay = options.toOverlayUiState(),
                        isLevelUpgradeInProgress = false,
                        errorMessage = null
                    )
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLevelUpgradeInProgress = false,
                        errorMessage = exception.message
                            ?: "Не удалось подготовить улучшение"
                    )
                }
            }
        }
    }

    private fun confirmLevelUpgrade() {
        if (_uiState.value.isLevelUpgradeInProgress) return

        val overlay = _uiState.value.levelUpgradeOverlay ?: return
        val selectedOption = overlay.selectedOption

        viewModelScope.launch {
            try {
                _uiState.update { state ->
                    state.copy(
                        isLevelUpgradeInProgress = true,
                        errorMessage = null
                    )
                }

                upgradePlayerEquipmentLevelsUseCase(
                    itemId = overlay.itemId,
                    levelsToUpgrade = selectedOption.levelsToUpgrade
                )

                _uiState.update { state ->
                    state.copy(
                        levelUpgradeOverlay = null,
                        isLevelUpgradeInProgress = false
                    )
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLevelUpgradeInProgress = false,
                        errorMessage = exception.message
                            ?: "Не удалось улучшить предмет"
                    )
                }
            }
        }
    }

    private fun decreaseSelectedUpgradeLevels() {
        _uiState.update { state ->
            val overlay = state.levelUpgradeOverlay ?: return@update state
            val previousLevels = overlay.selectedLevelsToUpgrade - 1

            if (overlay.options.none { it.levelsToUpgrade == previousLevels }) {
                return@update state
            }

            state.copy(
                levelUpgradeOverlay = overlay.copy(
                    selectedLevelsToUpgrade = previousLevels
                ),
                errorMessage = null
            )
        }
    }

    private fun increaseSelectedUpgradeLevels() {
        _uiState.update { state ->
            val overlay = state.levelUpgradeOverlay ?: return@update state
            val nextLevels = overlay.selectedLevelsToUpgrade + 1

            if (overlay.options.none { it.levelsToUpgrade == nextLevels }) {
                return@update state
            }

            state.copy(
                levelUpgradeOverlay = overlay.copy(
                    selectedLevelsToUpgrade = nextLevels
                ),
                errorMessage = null
            )
        }
    }

    private fun Item.toUiModel(
        isEquipped: Boolean
    ): EquipmentItemUiModel {
        val definition = getEquipmentDefinitionByIdUseCase(definitionId)
            ?: error("Не найдено определение снаряжения с id $definitionId")

        val primaryStats = toPrimaryStatsUiModel(definition)

        return EquipmentItemUiModel(
            itemId = id,
            slot = when (this) {
                is WeaponItem -> EquipmentSlot.WEAPON
                is ArmorItem -> EquipmentSlot.ARMOR
                is ArtifactItem -> EquipmentSlot.ARTIFACT
            },
            itemName = definition.name,
            icon = when (this) {
                is WeaponItem -> Icons.Outlined.GpsFixed
                is ArmorItem -> Icons.Outlined.Shield
                is ArtifactItem -> Icons.Outlined.AutoAwesome
            },
            level = level,
            maxLevel = maxLevel,
            quality = quality,
            primaryStats = primaryStats,
            additionalStat = toAdditionalStatUiModel(),
            isEquipped = isEquipped,
        )
    }

    private fun Item.toPrimaryStatsUiModel(
        definition: EquipmentDefinition
    ): List<EquipmentStatUiModel> {
        return when (this) {
            is WeaponItem -> listOf(
                EquipmentStatUiModel(
                    type = definition.primaryFirstStatType,
                    value = damage
                ),
                EquipmentStatUiModel(
                    type = definition.primarySecondStatType,
                    value = attackSpeed
                )
            )

            is ArmorItem -> listOf(
                EquipmentStatUiModel(
                    type = definition.primaryFirstStatType,
                    value = hp
                ),
                EquipmentStatUiModel(
                    type = definition.primarySecondStatType,
                    value = defense
                )
            )

            is ArtifactItem -> listOf(
                EquipmentStatUiModel(
                    type = definition.primaryFirstStatType,
                    value = cooldownReductionPercent
                ),
                EquipmentStatUiModel(
                    type = definition.primarySecondStatType,
                    value = durationBonusPercent
                )
            )
        }
    }

    private fun Item.toAdditionalStatUiModel(): EquipmentStatUiModel {
        return EquipmentStatUiModel(
            type = additionalStatType,
            value = additionalStatValue
        )
    }

    private fun EquipmentLevelUpgradeOptions.toOverlayUiState():
            EquipmentLevelUpgradeOverlayUiState {

        return EquipmentLevelUpgradeOverlayUiState(
            itemId = itemId,
            currentLevel = currentLevel,
            maxLevel = maxLevel,
            availableSilver = availableSilver,
            selectedLevelsToUpgrade = options.first().levelsToUpgrade,
            options = options.map { option ->
                EquipmentLevelUpgradeOptionUiModel(
                    levelsToUpgrade = option.levelsToUpgrade,
                    targetLevel = option.targetLevel,
                    totalCost = option.totalCost,
                    guaranteedStatChanges = option.guaranteedStatChanges.map { change ->
                        EquipmentStatUpgradeUiModel(
                            statType = change.statType,
                            currentValue = change.previousValue,
                            increment = change.updatedValue - change.previousValue
                        )
                    },
                    randomUpgradeLevels = option.randomUpgradeLevels
                )
            }
        )
    }
}