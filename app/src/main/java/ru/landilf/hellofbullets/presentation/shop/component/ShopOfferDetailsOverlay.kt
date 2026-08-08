package ru.landilf.hellofbullets.presentation.shop.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ru.landilf.hellofbullets.R
import ru.landilf.hellofbullets.presentation.common.overlay.OverlayCard
import ru.landilf.hellofbullets.presentation.common.overlay.OverlayContentColumn
import ru.landilf.hellofbullets.presentation.equipment.formatValue
import ru.landilf.hellofbullets.presentation.equipment.toStringRes
import ru.landilf.hellofbullets.presentation.shop.EquipmentStatUiModel
import ru.landilf.hellofbullets.presentation.shop.ShopOfferUiModel

@Composable
fun ShopOfferDetailsOverlay(
    offer: ShopOfferUiModel,
    onPurchaseClick: () -> Unit,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismissClick,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        OverlayCard(
            title = offer.itemName,
            modifier = modifier,
            cardHeightFraction = 0.7f,
            onCloseClick = onDismissClick,
            onBackgroundClick = onDismissClick
        ) {
            OverlayContentColumn(
                spacing = 12.dp,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.equipment_level, offer.level),
                    color = Color.White
                )

                Text(
                    text = stringResource(
                        R.string.equipment_property_value,
                        stringResource(R.string.equipment_quality),
                        stringResource(offer.quality.toStringRes())
                    ),
                    color = Color.White
                )

                offer.primaryStats.forEach { stat ->
                    EquipmentStatText(stat)
                }

                Text(
                    text = stringResource(R.string.equipment_additional_stat),
                    color = MaterialTheme.colorScheme.secondary
                )

                EquipmentStatText(offer.additionalStat)

                Text(
                    text = stringResource(
                        R.string.shop_price,
                        offer.purchasePrice
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onPurchaseClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.shop_buy)
                    )
                }
            }
        }
    }
}

@Composable
private fun EquipmentStatText(
    stat: EquipmentStatUiModel
) {
    Text(
        text = stringResource(
            R.string.equipment_property_value,
            stringResource(stat.type.toStringRes()),
            stat.type.formatValue(stat.value)
        ),
        color = Color.White
    )
}