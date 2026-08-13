package ru.landilf.hellofbullets.presentation.equipment

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.landilf.hellofbullets.R
import ru.landilf.hellofbullets.domain.model.player.EquipmentSlot
import ru.landilf.hellofbullets.presentation.common.CenteredMessage
import ru.landilf.hellofbullets.presentation.common.sheet.AppBottomSheet
import ru.landilf.hellofbullets.presentation.equipment.component.EquipmentItemCard
import ru.landilf.hellofbullets.presentation.equipment.component.EquipmentItemDetailsSheet
import ru.landilf.hellofbullets.presentation.equipment.component.EquipmentLevelUpgradeOverlay
import ru.landilf.hellofbullets.presentation.equipment.component.EquipmentSlotCard

@Composable
fun EquipmentScreen(
    state: EquipmentUiState,
    onAction: (EquipmentAction) -> Unit
) {
    val pagerState = rememberPagerState(
        pageCount = { EquipmentSlot.entries.size }
    )
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Text(
            text = stringResource(R.string.equipment_title),
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 12.dp
            ),
            style = MaterialTheme.typography.headlineMedium
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EquipmentSlot.entries.forEach { slot ->
                EquipmentSlotCard(
                    title = stringResource(slot.titleRes()),
                    itemName = state.equippedItemName(slot),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        PrimaryTabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            EquipmentSlot.entries.forEachIndexed { index, slot ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(stringResource(slot.tabTitleRes()))
                    }
                )
            }
        }

        state.errorMessage?.let { errorMessage ->
            Text(
                text = errorMessage,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.error
            )
        }

        if (state.isLoading) {
            CenteredMessage(
                message = stringResource(R.string.loading_title),
                modifier = Modifier.weight(1f)
            )
        } else {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                EquipmentItemList(
                    items = state.itemsFor(EquipmentSlot.entries[page]),
                    selectedItemId = state.selectedItemId,
                    onItemClick = { itemId ->
                        onAction(EquipmentAction.OnItemClick(itemId))
                    }
                )
            }
        }
    }

    if (state.isItemDetailsVisible) {
        state.selectedItem()?.let { item ->
            AppBottomSheet(
                onDismissRequest = {
                    onAction(EquipmentAction.OnItemDetailsDismiss)
                }
            ) {
                EquipmentItemDetailsSheet(
                    item = item,
                    onLevelUpgradeClick = {
                        onAction(EquipmentAction.OnLevelUpgradeClick)
                    },
                    onToggleEquipmentClick = {
                        onAction(EquipmentAction.OnToggleEquipmentClick)
                    },
                    isLevelUpgradeInProgress = state.isLevelUpgradeInProgress
                )
            }
        }
    }

    state.levelUpgradeOverlay?.let { overlayState ->
        EquipmentLevelUpgradeOverlay(
            state = overlayState,
            isUpgradeInProgress = state.isLevelUpgradeInProgress,
            onDecreaseClick = {
                onAction(EquipmentAction.OnLevelUpgradeLevelsDecrease)
            },
            onIncreaseClick = {
                onAction(EquipmentAction.OnLevelUpgradeLevelsIncrease)
            },
            onConfirmClick = {
                onAction(EquipmentAction.OnLevelUpgradeConfirmClick)
            },
            onDismissClick = {
                onAction(EquipmentAction.OnLevelUpgradeOverlayDismiss)
            },
            errorMessage = state.errorMessage
        )
    }
}

@Composable
private fun EquipmentItemList(
    items: List<EquipmentItemUiModel>,
    selectedItemId: Long?,
    onItemClick: (Long) -> Unit
) {
    if (items.isEmpty()) {
        CenteredMessage(
            message = stringResource(R.string.equipment_empty)
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = items,
            key = { item -> item.itemId }
        ) { item ->
            EquipmentItemCard(
                item = item,
                isSelected = item.itemId == selectedItemId,
                onClick = {
                    onItemClick(item.itemId)
                }
            )
        }
    }
}

private fun EquipmentUiState.itemsFor(
    slot: EquipmentSlot
): List<EquipmentItemUiModel> {
    return when (slot) {
        EquipmentSlot.WEAPON -> weaponItems
        EquipmentSlot.ARMOR -> armorItems
        EquipmentSlot.ARTIFACT -> artifactItems
    }
}

private fun EquipmentUiState.equippedItemName(
    slot: EquipmentSlot
): String? {
    val equippedItemId = when (slot) {
        EquipmentSlot.WEAPON -> equippedWeaponItemId
        EquipmentSlot.ARMOR -> equippedArmorItemId
        EquipmentSlot.ARTIFACT -> equippedArtifactItemId
    } ?: return null

    return itemsFor(slot)
        .firstOrNull { item ->
            item.itemId == equippedItemId
        }
        ?.itemName
}

@StringRes
private fun EquipmentSlot.titleRes(): Int {
    return when (this) {
        EquipmentSlot.WEAPON -> R.string.equipment_slot_weapon
        EquipmentSlot.ARMOR -> R.string.equipment_slot_armor
        EquipmentSlot.ARTIFACT -> R.string.equipment_slot_artifact
    }
}

@StringRes
private fun EquipmentSlot.tabTitleRes(): Int {
    return when (this) {
        EquipmentSlot.WEAPON -> R.string.equipment_tab_weapon
        EquipmentSlot.ARMOR -> R.string.equipment_tab_armor
        EquipmentSlot.ARTIFACT -> R.string.equipment_tab_artifact
    }
}