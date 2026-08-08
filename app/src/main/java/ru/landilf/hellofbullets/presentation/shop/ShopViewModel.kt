package ru.landilf.hellofbullets.presentation.shop

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.material.icons.outlined.Shield
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.landilf.hellofbullets.domain.model.equipment.ArmorItem
import ru.landilf.hellofbullets.domain.model.equipment.ArtifactItem
import ru.landilf.hellofbullets.domain.model.equipment.WeaponItem
import ru.landilf.hellofbullets.domain.model.shop.ManualShopRefreshPreviewResult
import ru.landilf.hellofbullets.domain.model.shop.ManualShopRefreshResult
import ru.landilf.hellofbullets.domain.model.shop.PurchaseShopOfferResult
import ru.landilf.hellofbullets.domain.model.shop.ShopOffer
import ru.landilf.hellofbullets.domain.model.shop.ShopState
import ru.landilf.hellofbullets.domain.usecase.equipment.GetEquipmentDefinitionByIdUseCase
import ru.landilf.hellofbullets.domain.usecase.shop.GetManualShopRefreshPreviewUseCase
import ru.landilf.hellofbullets.domain.usecase.shop.GetOrRefreshShopStateUseCase
import ru.landilf.hellofbullets.domain.usecase.shop.ObserveShopStateUseCase
import ru.landilf.hellofbullets.domain.usecase.shop.PurchaseShopOfferUseCase
import ru.landilf.hellofbullets.domain.usecase.shop.RefreshShopManuallyUseCase
import javax.inject.Inject

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val getOrRefreshShopStateUseCase: GetOrRefreshShopStateUseCase,
    private val observeShopStateUseCase: ObserveShopStateUseCase,
    private val purchaseShopOfferUseCase: PurchaseShopOfferUseCase,
    private val getEquipmentDefinitionByIdUseCase: GetEquipmentDefinitionByIdUseCase,
    private val refreshShopManuallyUseCase: RefreshShopManuallyUseCase,
    private val getManualShopRefreshPreviewUseCase: GetManualShopRefreshPreviewUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShopUiState())
    val uiState: StateFlow<ShopUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ShopEvent>()
    val events: SharedFlow<ShopEvent> = _events.asSharedFlow()

    init {
        observeShopState()
        loadShopState()
    }

    fun onAction(action: ShopAction) {
        when (action) {
            is ShopAction.OnOfferClick -> {
                selectOffer(action.itemId)
            }

            ShopAction.OnOfferDetailsDismiss -> {
                _uiState.update { currentState ->
                    currentState.copy(selectedOffer = null)
                }
            }

            ShopAction.OnPurchaseSelectedOfferClick -> {
                purchaseSelectedOffer()
            }

            ShopAction.OnManualRefreshClick -> {
                openManualRefreshConfirmation()
            }

            ShopAction.OnManualRefreshConfirmationDismiss -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        refreshConfirmation = null
                    )
                }
            }

            ShopAction.OnManualRefreshConfirmClick -> {
                refreshShopManually()
            }
        }
    }

    private fun observeShopState() {
        viewModelScope.launch {
            observeShopStateUseCase().collect { shopState ->
                if (shopState == null) {
                    return@collect
                }

                updateShopState(shopState)
            }
        }
    }

    private fun loadShopState() {
        viewModelScope.launch {
            try {
                val shopState = getOrRefreshShopStateUseCase()

                updateShopState(shopState)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        errorMessage = exception.message
                            ?: "Не удалось загрузить магазин"
                    )
                }
            }
        }
    }

    private fun updateShopState(shopState: ShopState) {
        val offerUiModels = shopState.offers.map { offer ->
            offer.toUiModel()
        }

        _uiState.update { currentState ->
            val selectedItemId = currentState.selectedOffer?.itemId
            val updatedSelectedOffer = offerUiModels
                .firstOrNull { it.itemId == selectedItemId }
                ?.takeUnless { it.isSold }

            currentState.copy(
                isLoading = false,
                offers = offerUiModels,
                manualRefreshCount = shopState.manualRefreshCount,
                selectedOffer = updatedSelectedOffer,
                errorMessage = null
            )
        }
    }

    private fun selectOffer(itemId: Long) {
        _uiState.update { currentState ->
            val offer = currentState.offers
                .firstOrNull { it.itemId == itemId }
                ?.takeUnless { it.isSold }

            currentState.copy(
                selectedOffer = offer,
                errorMessage = null
            )
        }
    }

    private fun purchaseSelectedOffer() {
        val selectedOffer = _uiState.value.selectedOffer ?: return

        viewModelScope.launch {
            try {
                when (purchaseShopOfferUseCase(itemId = selectedOffer.itemId)) {
                    is PurchaseShopOfferResult.Success -> {
                        _uiState.update { currentState ->
                            currentState.copy(
                                selectedOffer = null,
                                errorMessage = null
                            )
                        }

                        _events.emit(ShopEvent.OfferPurchased)
                    }

                    is PurchaseShopOfferResult.InsufficientSilver -> {
                        _uiState.update { currentState ->
                            currentState.copy(
                                selectedOffer = null
                            )
                        }

                        _events.emit(ShopEvent.InsufficientSilver)
                    }

                    PurchaseShopOfferResult.OfferNotFound,
                    PurchaseShopOfferResult.OfferAlreadySold -> {
                        _uiState.update { currentState ->
                            currentState.copy(selectedOffer = null)
                        }

                        _events.emit(ShopEvent.OfferUnavailable)
                    }
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        errorMessage = exception.message
                            ?: "Не удалось купить предмет"
                    )
                }
            }
        }
    }

    private fun openManualRefreshConfirmation() {
        if (_uiState.value.isManualRefreshInProgress) {
            return
        }

        viewModelScope.launch {
            try {
                when (val result = getManualShopRefreshPreviewUseCase()) {
                    is ManualShopRefreshPreviewResult.Available -> {
                        _uiState.update { currentState ->
                            currentState.copy(
                                refreshConfirmation = ShopRefreshConfirmationUiModel(
                                    refreshCost = result.refreshCost,
                                    remainingRefreshCount = result.remainingRefreshCount
                                ),
                                errorMessage = null
                            )
                        }
                    }

                    ManualShopRefreshPreviewResult.DailyLimitReached -> {
                        _events.emit(ShopEvent.ManualRefreshLimitReached)
                    }
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        errorMessage = exception.message
                            ?: "Не удалось подготовить обновление магазина"
                    )
                }
            }
        }
    }

    private fun refreshShopManually() {
        if (_uiState.value.isManualRefreshInProgress) {
            return
        }

        _uiState.update { currentState ->
            currentState.copy(
                isManualRefreshInProgress = true
            )
        }

        viewModelScope.launch {
            try {
                when (val result = refreshShopManuallyUseCase()) {
                    is ManualShopRefreshResult.Success -> {
                        updateShopState(result.shopState)
                        closeManualRefreshConfirmation()
                        _events.emit(ShopEvent.ShopRefreshed)
                    }

                    is ManualShopRefreshResult.InsufficientSilver -> {
                        closeManualRefreshConfirmation()
                        _events.emit(ShopEvent.InsufficientSilver)
                    }

                    ManualShopRefreshResult.DailyLimitReached -> {
                        closeManualRefreshConfirmation()
                        _events.emit(ShopEvent.ManualRefreshLimitReached)
                    }
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                closeManualRefreshConfirmation()
                _uiState.update { currentState ->
                    currentState.copy(
                        errorMessage = exception.message
                            ?: "Не удалось обновить магазин"
                    )
                }
            }
        }
    }

    private fun closeManualRefreshConfirmation() {
        _uiState.update { currentState ->
            currentState.copy(
                refreshConfirmation = null,
                isManualRefreshInProgress = false
            )
        }
    }

    private fun ShopOffer.toUiModel(): ShopOfferUiModel {
        val definition = getEquipmentDefinitionByIdUseCase(item.definitionId)
            ?: throw IllegalArgumentException(
                "Не найдено определение снаряжения с id ${item.definitionId}"
            )

        val primaryStats = when (val equipment = item) {
            is WeaponItem -> listOf(
                EquipmentStatUiModel(
                    type = definition.primaryFirstStatType,
                    value = equipment.damage
                ),
                EquipmentStatUiModel(
                    type = definition.primarySecondStatType,
                    value = equipment.attackSpeed
                )
            )

            is ArmorItem -> listOf(
                EquipmentStatUiModel(
                    type = definition.primaryFirstStatType,
                    value = equipment.hp
                ),
                EquipmentStatUiModel(
                    type = definition.primarySecondStatType,
                    value = equipment.defense
                )
            )

            is ArtifactItem -> listOf(
                EquipmentStatUiModel(
                    type = definition.primaryFirstStatType,
                    value = equipment.cooldownReductionPercent
                ),
                EquipmentStatUiModel(
                    type = definition.primarySecondStatType,
                    value = equipment.durationBonusPercent
                )
            )
        }

        return ShopOfferUiModel(
            itemId = item.id,
            itemName = definition.name,
            icon = when (item) {
                is WeaponItem -> Icons.Outlined.GpsFixed
                is ArmorItem -> Icons.Outlined.Shield
                is ArtifactItem -> Icons.Outlined.AutoAwesome
            },
            level = item.level,
            quality = item.quality,
            primaryStats = primaryStats,
            additionalStat = EquipmentStatUiModel(
                type = item.additionalStatType,
                value = item.additionalStatValue
            ),
            purchasePrice = purchasePrice,
            isSold = isSold
        )
    }
}