package ru.landilf.hellofbullets.presentation.shop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.Flow
import ru.landilf.hellofbullets.R
import ru.landilf.hellofbullets.domain.model.shop.ShopState
import ru.landilf.hellofbullets.presentation.common.CenteredMessage
import ru.landilf.hellofbullets.presentation.shop.component.ShopOfferCard
import ru.landilf.hellofbullets.presentation.shop.component.ShopOfferDetailsOverlay
import ru.landilf.hellofbullets.presentation.shop.component.ShopRefreshConfirmationOverlay

@Composable
fun ShopScreen(
    state: ShopUiState,
    events: Flow<ShopEvent>,
    onAction: (ShopAction) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val purchaseSuccessMessage = stringResource(R.string.shop_purchase_success)
    val insufficientSilverMessage = stringResource(R.string.shop_insufficient_silver)
    val offerUnavailableMessage = stringResource(R.string.shop_offer_unavailable)
    val shopRefreshedMessage = stringResource(R.string.shop_refresh_success)
    val manualRefreshLimitMessage = stringResource(R.string.shop_refresh_limit_reached)

    LaunchedEffect(events) {
        events.collect { event ->
            val message = when (event) {
                ShopEvent.OfferPurchased -> purchaseSuccessMessage
                ShopEvent.InsufficientSilver -> insufficientSilverMessage
                ShopEvent.OfferUnavailable -> offerUnavailableMessage
                ShopEvent.ShopRefreshed -> shopRefreshedMessage
                ShopEvent.ManualRefreshLimitReached -> manualRefreshLimitMessage
            }

            snackbarHostState.showSnackbar(message)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (state.isLoading) {
            CenteredMessage(
                message = stringResource(R.string.loading_title)
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 12.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.shop_title),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                onAction(ShopAction.OnManualRefreshClick)
                            },
                            enabled =
                                state.manualRefreshCount < ShopState.MAX_MANUAL_REFRESH_COUNT &&
                                        !state.isManualRefreshInProgress
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Refresh,
                                contentDescription = null
                            )

                            Text(
                                text = stringResource(R.string.shop_refresh),
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }

                        Text(
                            text = stringResource(
                                R.string.shop_refresh_count,
                                state.manualRefreshCount,
                                ShopState.MAX_MANUAL_REFRESH_COUNT
                            ),
                            modifier = Modifier.padding(start = 8.dp),
                            fontSize = 14.sp
                        )
                    }
                }

                state.errorMessage?.let { errorMessage ->
                    Text(
                        text = errorMessage,
                        modifier = Modifier.padding(
                            horizontal = 16.dp
                        ),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                BoxWithConstraints(
                    modifier = Modifier.weight(1f)
                ) {
                    val verticalPadding = 16.dp
                    val verticalSpacing = 8.dp
                    val itemHeight = (maxHeight -
                            verticalPadding * 2 - verticalSpacing * 3) / 4

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            horizontal = 16.dp,
                            vertical = verticalPadding
                        ),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(verticalSpacing)
                    ) {
                        items(
                            items = state.offers,
                            key = { offer -> offer.itemId }
                        ) { offer ->
                            ShopOfferCard(
                                offer = offer,
                                onClick = {
                                    onAction(
                                        ShopAction.OnOfferClick(
                                            itemId = offer.itemId
                                        )
                                    )
                                },
                                modifier = Modifier.height(itemHeight)
                            )
                        }
                    }
                }
            }
        }

        state.selectedOffer?.let { selectedOffer ->
            ShopOfferDetailsOverlay(
                offer = selectedOffer,
                onPurchaseClick = {
                    onAction(
                        ShopAction.OnPurchaseSelectedOfferClick
                    )
                },
                onDismissClick = {
                    onAction(
                        ShopAction.OnOfferDetailsDismiss
                    )
                }
            )
        }

        state.refreshConfirmation?.let { confirmation ->
            ShopRefreshConfirmationOverlay(
                confirmation = confirmation,
                isRefreshing = state.isManualRefreshInProgress,
                onConfirmClick = { onAction(ShopAction.OnManualRefreshConfirmClick) },
                onDismissClick = { onAction(ShopAction.OnManualRefreshConfirmationDismiss) }

            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}