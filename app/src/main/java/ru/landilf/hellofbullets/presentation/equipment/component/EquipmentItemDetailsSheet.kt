package ru.landilf.hellofbullets.presentation.equipment.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.landilf.hellofbullets.R
import ru.landilf.hellofbullets.presentation.common.equipment.EquipmentStatUiModel
import ru.landilf.hellofbullets.presentation.equipment.EquipmentItemUiModel
import ru.landilf.hellofbullets.presentation.equipment.formatValue
import ru.landilf.hellofbullets.presentation.equipment.toStringRes

@Composable
fun EquipmentItemDetailsSheet(
    item: EquipmentItemUiModel,
    onToggleEquipmentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = item.itemName,
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = stringResource(
                        R.string.equipment_level_progress,
                        item.level,
                        item.maxLevel
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Text(
            text = stringResource(item.quality.toStringRes()),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        HorizontalDivider()

        item.primaryStats.forEach { stat ->
            EquipmentStatRow(stat = stat)
        }

        Text(
            text = stringResource(R.string.equipment_additional_stat),
            style = MaterialTheme.typography.titleSmall
        )

        EquipmentStatRow(stat = item.additionalStat)

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = if (item.isEquipped) {
                stringResource(R.string.equipment_equipped)
            } else {
                stringResource(R.string.equipment_not_equipped)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Button(
            onClick = onToggleEquipmentClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                stringResource(
                    if (item.isEquipped) {
                        R.string.equipment_unequip
                    } else {
                        R.string.equipment_equip
                    }
                )
            )
        }
    }
}

@Composable
private fun EquipmentStatRow(
    stat: EquipmentStatUiModel
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(stat.type.toStringRes()),
            modifier = Modifier.weight(1f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = stat.type.formatValue(stat.value),
            modifier = Modifier.widthIn(min = 72.dp),
            textAlign = TextAlign.End,
            fontWeight = FontWeight.Bold
        )
    }
}