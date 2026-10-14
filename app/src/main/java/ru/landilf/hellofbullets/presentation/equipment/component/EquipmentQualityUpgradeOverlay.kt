package ru.landilf.hellofbullets.presentation.equipment.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ru.landilf.hellofbullets.R
import ru.landilf.hellofbullets.presentation.common.equipment.toIcon
import ru.landilf.hellofbullets.presentation.common.overlay.OverlayCard
import ru.landilf.hellofbullets.presentation.common.window.HideDialogSystemBars
import ru.landilf.hellofbullets.presentation.equipment.EquipmentItemUiModel
import ru.landilf.hellofbullets.presentation.equipment.EquipmentQualityUpgradeOverlayUiState
import ru.landilf.hellofbullets.presentation.equipment.EquipmentQualityUpgradeResultUiState
import ru.landilf.hellofbullets.presentation.equipment.formatValue
import ru.landilf.hellofbullets.presentation.equipment.toStringRes

@Composable
fun EquipmentQualityUpgradeOverlay(
    state: EquipmentQualityUpgradeOverlayUiState,
    result: EquipmentQualityUpgradeResultUiState?,
    isUpgradeInProgress: Boolean,
    onMaterialClick: (Long) -> Unit,
    onConfirmClick: () -> Unit,
    errorMessage: String?,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = {
            if (!isUpgradeInProgress) {
                onDismissClick()
            }
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        HideDialogSystemBars()

        OverlayCard(
            title = stringResource(
                if (result == null) {
                    R.string.equipment_quality_upgrade_overlay_title
                } else {
                    R.string.equipment_quality_upgrade_result_title
                }
            ),
            modifier = modifier,
            cardHeightFraction = 0.8f,
            onCloseClick = if (isUpgradeInProgress) null else onDismissClick,
            onBackgroundClick = if (isUpgradeInProgress) null else onDismissClick
        ) {
            if (result == null) {
                QualityUpgradeContent(
                    state = state,
                    isUpgradeInProgress = isUpgradeInProgress,
                    onMaterialClick = onMaterialClick,
                    onConfirmClick = onConfirmClick,
                    errorMessage = errorMessage,
                )
            } else {
                QualityUpgradeResultContent(
                    result = result,
                    onDoneClick = onDismissClick
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.QualityUpgradeContent(
    state: EquipmentQualityUpgradeOverlayUiState,
    isUpgradeInProgress: Boolean,
    onMaterialClick: (Long) -> Unit,
    onConfirmClick: () -> Unit,
    errorMessage: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val currentQualityName = stringResource(state.currentQuality.toStringRes())
        val nextQualityName = stringResource(state.nextQuality.toStringRes())

        Text(
            text = stringResource(
                R.string.equipment_quality_upgrade_range,
                currentQualityName,
                nextQualityName
            ),
            color = Color.White
        )

        Text(
            text = stringResource(
                R.string.equipment_quality_upgrade_materials,
                state.availableMaterialsCount,
                state.requiredMaterialsCount
            ),
            color = Color.White
        )

        Text(
            text = stringResource(
                R.string.equipment_quality_upgrade_selected_materials,
                state.selectedMaterialsCount,
                state.requiredMaterialsCount
            ),
            color = Color.White
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(
                items = state.materialItems,
                key = { item -> item.itemId }
            ) { item ->
                val isSelected = item.itemId in state.selectedMaterialItemIds
                val isEnabled =
                    isSelected || state.selectedMaterialsCount < state.requiredMaterialsCount

                QualityUpgradeMaterialRow(
                    item = item,
                    isSelected = isSelected,
                    enabled = isEnabled && !isUpgradeInProgress,
                    onClick = { onMaterialClick(item.itemId) }
                )
            }
        }

        errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = state.primaryFirstStatPreview.statType.toIcon(),
                contentDescription = null,
                tint = Color.White
            )

            Text(
                text = stringResource(
                    R.string.equipment_quality_upgrade_stat_preview,
                    stringResource(state.primaryFirstStatPreview.statType.toStringRes()),
                    state.primaryFirstStatPreview.statType.formatValue(
                        state.primaryFirstStatPreview.currentValue
                    ),
                    state.primaryFirstStatPreview.statType.formatValue(
                        state.primaryFirstStatPreview.upgradedValue
                    )
                ),
                color = Color.White
            )
        }

        Button(
            onClick = onConfirmClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = state.canConfirm && !isUpgradeInProgress
        ) {
            Text(stringResource(R.string.equipment_quality_upgrade_confirm))
        }
    }
}

@Composable
private fun QualityUpgradeMaterialRow(
    item: EquipmentItemUiModel,
    isSelected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = enabled,
                onClick = onClick
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onClick() },
            enabled = enabled
        )

        Column(
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(
                text = item.itemName,
                color = Color.White
            )

            Text(
                text = stringResource(
                    R.string.equipment_level_progress,
                    item.level,
                    item.maxLevel
                ),
                color = Color.LightGray
            )
        }
    }
}

@Composable
private fun ColumnScope.QualityUpgradeResultContent(
    result: EquipmentQualityUpgradeResultUiState,
    onDoneClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(
                R.string.equipment_quality_upgrade_range,
                stringResource(result.previousQuality.toStringRes()),
                stringResource(result.upgradedQuality.toStringRes())
            ),
            color = Color.White
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = result.primaryFirstStat.statType.toIcon(),
                contentDescription = null,
                tint = Color.White
            )

            Text(
                text = stringResource(
                    R.string.equipment_quality_upgrade_stat_preview,
                    stringResource(result.primaryFirstStat.statType.toStringRes()),
                    result.primaryFirstStat.statType.formatValue(
                        result.primaryFirstStat.currentValue
                    ),
                    result.primaryFirstStat.statType.formatValue(
                        result.primaryFirstStat.upgradedValue
                    )
                ),
                color = Color.White
            )
        }

        Text(
            text = stringResource(
                R.string.equipment_quality_upgrade_consumed_materials
            ),
            color = Color.White
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(
                items = result.consumedMaterialItems,
                key = { item -> item.itemId }
            ) { item ->
                QualityUpgradeConsumedMaterialRow(item)
            }
        }

        Button(
            onClick = onDoneClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.done_button))
        }
    }
}

@Composable
private fun QualityUpgradeConsumedMaterialRow(
    item: EquipmentItemUiModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = item.itemName,
                color = Color.White
            )

            Text(
                text = stringResource(
                    R.string.equipment_level_progress,
                    item.level,
                    item.maxLevel
                ),
                color = Color.LightGray
            )
        }
    }
}