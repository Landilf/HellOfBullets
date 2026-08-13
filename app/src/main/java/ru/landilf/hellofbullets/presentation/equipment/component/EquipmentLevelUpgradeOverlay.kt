package ru.landilf.hellofbullets.presentation.equipment.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ru.landilf.hellofbullets.R
import ru.landilf.hellofbullets.presentation.common.overlay.OverlayCard
import ru.landilf.hellofbullets.presentation.common.window.HideDialogSystemBars
import ru.landilf.hellofbullets.presentation.equipment.EquipmentLevelUpgradeOverlayUiState
import ru.landilf.hellofbullets.presentation.equipment.EquipmentStatUpgradeUiModel
import ru.landilf.hellofbullets.presentation.equipment.formatValue
import ru.landilf.hellofbullets.presentation.equipment.toStringRes

@Composable
fun EquipmentLevelUpgradeOverlay(
    state: EquipmentLevelUpgradeOverlayUiState,
    isUpgradeInProgress: Boolean,
    onDecreaseClick: () -> Unit,
    onIncreaseClick: () -> Unit,
    onConfirmClick: () -> Unit,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String?
) {
    val selectedOption = state.selectedOption
    val canDecrease = selectedOption.levelsToUpgrade > 1
    val canIncrease = state.options.any { option ->
        option.levelsToUpgrade == selectedOption.levelsToUpgrade + 1
    }

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
            title = stringResource(R.string.equipment_upgrade_overlay_title),
            modifier = modifier,
            cardHeightFraction = 0.72f,
            onCloseClick = if (isUpgradeInProgress) null else onDismissClick,
            onBackgroundClick = if (isUpgradeInProgress) null else onDismissClick
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
                        R.string.equipment_upgrade_level_range,
                        state.currentLevel,
                        selectedOption.targetLevel,
                        state.maxLevel
                    ),
                    color = Color.White
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(
                        onClick = onDecreaseClick,
                        enabled = canDecrease && !isUpgradeInProgress
                    ) {
                        Text("-")
                    }

                    Text(
                        text = stringResource(
                            R.string.equipment_upgrade_selected_levels,
                            selectedOption.levelsToUpgrade
                        ),
                        color = Color.White
                    )

                    OutlinedButton(
                        onClick = onIncreaseClick,
                        enabled = canIncrease && !isUpgradeInProgress
                    ) {
                        Text("+")
                    }
                }

                Text(
                    text = stringResource(
                        R.string.equipment_upgrade_cost,
                        selectedOption.totalCost
                    ),
                    color = Color.White
                )

                Text(
                    text = stringResource(
                        R.string.equipment_upgrade_available_silver,
                        state.availableSilver
                    ),
                    color = Color.White
                )

                Text(
                    text = stringResource(R.string.equipment_upgrade_guaranteed_stats),
                    color = Color.White
                )

                selectedOption.guaranteedStatChanges.forEach { change ->
                    GuaranteedStatChangeRow(change)
                }

                if (selectedOption.randomUpgradeLevels.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.equipment_upgrade_random_upgrades),
                        color = Color.White
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(selectedOption.randomUpgradeLevels) { level ->
                            Text(
                                text = stringResource(
                                    R.string.equipment_upgrade_random_level,
                                    level
                                ),
                                color = Color.White
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                errorMessage?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }

                Button(
                    onClick = onConfirmClick,
                    enabled = !isUpgradeInProgress,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.confirm_button))
                }
            }
        }
    }
}

@Composable
private fun GuaranteedStatChangeRow(
    change: EquipmentStatUpgradeUiModel
) {
    Text(
        text = stringResource(
            R.string.equipment_upgrade_stat_change,
            stringResource(change.statType.toStringRes()),
            change.statType.formatValue(change.currentValue),
            change.statType.formatValue(change.increment)
        ),
        color = Color.White
    )
}