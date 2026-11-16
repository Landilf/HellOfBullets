package ru.landilf.hellofbullets.presentation.skills.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.landilf.hellofbullets.R
import ru.landilf.hellofbullets.domain.model.skills.SkillSlot
import ru.landilf.hellofbullets.presentation.skills.SkillUiModel

@Composable
fun SkillCard(
    skill: SkillUiModel,
    isActionInProgress: Boolean,
    onUnlockClick: () -> Unit,
    onUpgradeClick: () -> Unit,
    onEquipClick: (SkillSlot) -> Unit,
    onUnequipClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (skill.equippedSlot != null) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(skill.nameRes),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = stringResource(skill.descriptionRes),
                style = MaterialTheme.typography.bodyMedium
            )

            if (skill.isUnlocked) {
                val level = requireNotNull(skill.level)

                Text(
                    text = stringResource(
                        R.string.skills_level,
                        level,
                        skill.maxLevel
                    ),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                skill.equippedSlot?.let { slot ->
                    Text(
                        text = stringResource(slot.titleRes()),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                Text(
                    text = stringResource(R.string.skills_locked),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }

            SkillCardActions(
                skill = skill,
                isActionInProgress = isActionInProgress,
                onUnlockClick = onUnlockClick,
                onUpgradeClick = onUpgradeClick,
                onEquipClick = onEquipClick,
                onUnequipClick = onUnequipClick
            )
        }
    }
}

@Composable
private fun SkillCardActions(
    skill: SkillUiModel,
    isActionInProgress: Boolean,
    onUnlockClick: () -> Unit,
    onUpgradeClick: () -> Unit,
    onEquipClick: (SkillSlot) -> Unit,
    onUnequipClick: () -> Unit
) {
    if (!skill.isUnlocked) {
        Button(
            onClick = onUnlockClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isActionInProgress
        ) {
            Text(
                text = stringResource(
                    R.string.skills_unlock,
                    skill.unlockCost
                )
            )
        }
        return
    }

    skill.nextUpgradeCost?.let { cost ->
        OutlinedButton(
            onClick = onUpgradeClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isActionInProgress
        ) {
            Text(
                text = stringResource(
                    R.string.skills_upgrade,
                    cost
                )
            )
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = {
                onEquipClick(SkillSlot.FIRST)
            },
            modifier = Modifier.weight(1f),
            enabled = !isActionInProgress
        ) {
            Text(stringResource(R.string.skills_equip_first))
        }

        OutlinedButton(
            onClick = {
                onEquipClick(SkillSlot.SECOND)
            },
            modifier = Modifier.weight(1f),
            enabled = !isActionInProgress
        ) {
            Text(stringResource(R.string.skills_equip_second))
        }
    }

    if (skill.equippedSlot != null) {
        TextButton(
            onClick = onUnequipClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isActionInProgress
        ) {
            Text(stringResource(R.string.skills_unequip))
        }
    }
}

private fun SkillSlot.titleRes(): Int {
    return when (this) {
        SkillSlot.FIRST -> R.string.skills_slot_first
        SkillSlot.SECOND -> R.string.skills_slot_second
    }
}