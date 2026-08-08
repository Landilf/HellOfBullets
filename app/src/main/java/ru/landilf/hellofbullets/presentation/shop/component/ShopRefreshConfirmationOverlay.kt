package ru.landilf.hellofbullets.presentation.shop.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ru.landilf.hellofbullets.R
import ru.landilf.hellofbullets.presentation.common.overlay.OverlayCard
import ru.landilf.hellofbullets.presentation.common.overlay.OverlayContentColumn
import ru.landilf.hellofbullets.presentation.shop.ShopRefreshConfirmationUiModel

@Composable
fun ShopRefreshConfirmationOverlay(
    confirmation: ShopRefreshConfirmationUiModel,
    isRefreshing: Boolean,
    onConfirmClick: () -> Unit,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = {
            if (!isRefreshing) {
                onDismissClick()
            }
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        OverlayCard(
            title = stringResource(R.string.shop_refresh_confirmation_title),
            modifier = modifier,
            cardHeightFraction = 0.45f,
            onCloseClick = if (isRefreshing) {
                null
            } else {
                onDismissClick
            },
            onBackgroundClick = if (isRefreshing) {
                null
            } else {
                onDismissClick
            }
        ) {
            OverlayContentColumn(
                spacing = 16.dp,
                modifier = Modifier.weight(1f)
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = stringResource(
                        R.string.shop_refresh_confirmation_message,
                        confirmation.refreshCost
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = stringResource(
                        R.string.shop_refresh_remaining_count,
                        confirmation.remainingRefreshCount
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onConfirmClick,
                    enabled = !isRefreshing,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isRefreshing) {
                            stringResource(R.string.loading_title)
                        } else {
                            stringResource(R.string.confirm_button)
                        }
                    )
                }
            }
        }
    }
}