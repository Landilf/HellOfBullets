package ru.landilf.hellofbullets.presentation.equipment.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.landilf.hellofbullets.R

@Composable
fun EquipmentSlotCard(
    title: String,
    itemName: String?,
    modifier: Modifier = Modifier
) {
    val isOccupied = itemName != null

    Card(
        modifier = modifier,
        border = BorderStroke(
            width = 1.dp,
            color = if (isOccupied) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline
            }
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isOccupied) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )

            Text(
                text = itemName ?: stringResource(R.string.equipment_not_selected),
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}