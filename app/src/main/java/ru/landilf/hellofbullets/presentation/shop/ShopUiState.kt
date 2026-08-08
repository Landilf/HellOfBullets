package ru.landilf.hellofbullets.presentation.shop

data class ShopUiState(
    val isLoading: Boolean = true,
    val offers: List<ShopOfferUiModel> = emptyList(),
    val selectedOffer: ShopOfferUiModel? = null,
    val manualRefreshCount: Int = 0,
    val refreshConfirmation: ShopRefreshConfirmationUiModel? = null,
    val isManualRefreshInProgress: Boolean = false,
    val errorMessage: String? = null
)
