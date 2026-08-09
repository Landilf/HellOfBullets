package ru.landilf.hellofbullets.presentation.equipment.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.landilf.hellofbullets.R
import ru.landilf.hellofbullets.presentation.equipment.EquipmentItemUiModel
import ru.landilf.hellofbullets.presentation.equipment.toStringRes

@Composable
fun EquipmentItemCard(
    item: EquipmentItemUiModel,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        border = if (isSelected) {
            BorderStroke(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            null
        },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = item.itemName,
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    text = stringResource(R.string.equipment_level, item.level),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = stringResource(item.quality.toStringRes()),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (item.isEquipped) {
                Text(
                    text = stringResource(R.string.equipment_equipped),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}