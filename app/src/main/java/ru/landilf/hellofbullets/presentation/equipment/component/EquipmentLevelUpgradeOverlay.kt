package ru.landilf.hellofbullets.presentation.equipment.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ru.landilf.hellofbullets.R
import ru.landilf.hellofbullets.domain.model.equipment.upgrade.EquipmentStatSource
import ru.landilf.hellofbullets.presentation.common.equipment.toIcon
import ru.landilf.hellofbullets.presentation.common.overlay.OverlayCard
import ru.landilf.hellofbullets.presentation.common.window.HideDialogSystemBars
import ru.landilf.hellofbullets.presentation.equipment.EquipmentLevelUpgradeOverlayUiState
import ru.landilf.hellofbullets.presentation.equipment.EquipmentLevelUpgradeResultUiState
import ru.landilf.hellofbullets.presentation.equipment.EquipmentStatUpgradeUiModel
import ru.landilf.hellofbullets.presentation.equipment.formatValue
import ru.landilf.hellofbullets.presentation.equipment.toStringRes

@Composable
fun EquipmentLevelUpgradeOverlay(
    state: EquipmentLevelUpgradeOverlayUiState,
    isUpgradeInProgress: Boolean,
    result: EquipmentLevelUpgradeResultUiState?,
    currentStepIndex: Int,
    levelUpgradeStepDurationMs: Int,
    isResultAnimating: Boolean,
    onDecreaseClick: () -> Unit,
    onIncreaseClick: () -> Unit,
    onConfirmClick: () -> Unit,
    onDismissClick: () -> Unit,
    onAnimationAccelerate: () -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String?
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
            title = stringResource(R.string.equipment_upgrade_overlay_title),
            modifier = modifier,
            cardHeightFraction = 0.72f,
            onCloseClick = if (isUpgradeInProgress) null else onDismissClick,
            onBackgroundClick = if (isUpgradeInProgress) null else onDismissClick
        ) {
            if (result == null) {
                UpgradeSelectionContent(
                    state = state,
                    isUpgradeInProgress = isUpgradeInProgress,
                    onDecreaseClick = onDecreaseClick,
                    onIncreaseClick = onIncreaseClick,
                    onConfirmClick = onConfirmClick,
                    errorMessage = errorMessage
                )
            } else {
                UpgradeResultContent(
                    result = result,
                    currentStepIndex = currentStepIndex,
                    levelUpgradeStepDurationMs = levelUpgradeStepDurationMs,
                    isResultAnimating = isResultAnimating,
                    onAnimationAccelerate = onAnimationAccelerate,
                    onDismissClick = onDismissClick
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.UpgradeSelectionContent(
    state: EquipmentLevelUpgradeOverlayUiState,
    isUpgradeInProgress: Boolean,
    onDecreaseClick: () -> Unit,
    onIncreaseClick: () -> Unit,
    onConfirmClick: () -> Unit,
    errorMessage: String?
) {
    val selectedOption = state.selectedOption
    val canDecrease = selectedOption.levelsToUpgrade > 1
    val canIncrease = state.options.any { option ->
        option.levelsToUpgrade == selectedOption.levelsToUpgrade + 1
    }

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

@Composable
private fun ColumnScope.UpgradeResultContent(
    result: EquipmentLevelUpgradeResultUiState,
    currentStepIndex: Int,
    levelUpgradeStepDurationMs: Int,
    isResultAnimating: Boolean,
    onAnimationAccelerate: () -> Unit,
    onDismissClick: () -> Unit
) {
    val currentStep = result.steps.getOrNull(currentStepIndex) ?: return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .clickable(
                enabled = isResultAnimating,
                onClick = onAnimationAccelerate
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(
                R.string.equipment_upgrade_result_level_range,
                result.originalLevel,
                result.upgradedLevel
            ),
            color = Color.White
        )

        Text(
            text = stringResource(
                R.string.equipment_upgrade_result_spent_silver,
                result.spentSilver
            ),
            color = Color.White
        )

        EquipmentUpgradeProgression(
            result = result,
            currentStepIndex = currentStepIndex
        )

        Text(
            text = stringResource(
                R.string.equipment_upgrade_result_step,
                currentStep.targetLevel
            ),
            color = Color.White
        )

        currentStep.statChanges.forEach { change ->
            key(currentStep.targetLevel, change.source) {
                AnimatedStatChangeRow(
                    change = change,
                    animationDurationMs = levelUpgradeStepDurationMs
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onDismissClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.done_button))
        }
    }
}

@Composable
private fun EquipmentUpgradeProgression(
    result: EquipmentLevelUpgradeResultUiState,
    currentStepIndex: Int
) {
    val listState = rememberLazyListState()

    LaunchedEffect(currentStepIndex) {
        if (currentStepIndex in result.steps.indices) {
            listState.animateScrollToItem(currentStepIndex)
        }
    }

    LazyRow(
        state = listState,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        itemsIndexed(
            items = result.steps,
            key = { _, step -> step.targetLevel }
        ) { index, step ->
            val randomStatType = step.statChanges
                .firstOrNull { change ->
                    change.source != EquipmentStatSource.PRIMARY_FIRST
                }
                ?.statType

            val primaryFirstStatType = step.statChanges
                .firstOrNull { change ->
                    change.source == EquipmentStatSource.PRIMARY_FIRST
                }
                ?.statType

            val displayedStatType = when {
                randomStatType != null && index <= currentStepIndex -> randomStatType
                randomStatType != null -> null
                else -> primaryFirstStatType
            }

            val isCurrent = index == currentStepIndex
            val color = if (isCurrent) {
                MaterialTheme.colorScheme.primary
            } else {
                Color.White
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = step.targetLevel.toString(),
                    color = color
                )

                if (displayedStatType == null) {
                    Text(
                        text = "?",
                        color = color
                    )
                } else {
                    Icon(
                        imageVector = displayedStatType.toIcon(),
                        contentDescription = stringResource(displayedStatType.toStringRes()),
                        modifier = Modifier.size(20.dp),
                        tint = color
                    )
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

@Composable
private fun AnimatedStatChangeRow(
    change: EquipmentStatUpgradeUiModel,
    animationDurationMs: Int
) {
    val animatedValue = remember(change) {
        Animatable(change.currentValue)
    }
    val animatedIncrement = remember(change) {
        Animatable(change.increment)
    }

    LaunchedEffect(change, animationDurationMs) {
        coroutineScope {
            launch {
                animatedValue.animateTo(
                    targetValue = change.currentValue + change.increment,
                    animationSpec = tween(
                        durationMillis = animationDurationMs
                    )
                )
            }

            launch {
                animatedIncrement.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = animationDurationMs
                    )
                )
            }
        }
    }

    Text(
        text = stringResource(
            R.string.equipment_upgrade_stat_change,
            stringResource(change.statType.toStringRes()),
            change.statType.formatValue(animatedValue.value),
            change.statType.formatValue(animatedIncrement.value)
        ),
        color = Color.White
    )
}