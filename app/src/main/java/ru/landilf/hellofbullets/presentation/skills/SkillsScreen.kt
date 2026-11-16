package ru.landilf.hellofbullets.presentation.skills

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.landilf.hellofbullets.R
import ru.landilf.hellofbullets.domain.model.skills.SkillSlot
import ru.landilf.hellofbullets.presentation.common.CenteredMessage
import ru.landilf.hellofbullets.presentation.skills.component.SkillCard
import ru.landilf.hellofbullets.presentation.skills.component.SkillsSlotCard

@Composable
fun SkillsScreen(
    state: SkillUiState,
    onAction: (SkillAction) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.main_menu_skills),
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 12.dp
            ),
            style = MaterialTheme.typography.headlineMedium
        )

        state.errorMessage?.let { errorMessage ->
            Text(
                text = errorMessage,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.error
            )
        }

        if (state.isLoading) {
            CenteredMessage(
                message = stringResource(R.string.loading_title),
                modifier = Modifier.weight(1f)
            )
            return
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = stringResource(
                        R.string.skills_points,
                        state.skillPointAmount
                    ),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            item {
                Text(
                    text = stringResource(R.string.skills_slots_title),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SkillsSlotCard(
                        title = stringResource(R.string.skills_slot_first),
                        skill = state.equippedSkill(SkillSlot.FIRST),
                        modifier = Modifier.weight(1f)
                    )

                    SkillsSlotCard(
                        title = stringResource(R.string.skills_slot_second),
                        skill = state.equippedSkill(SkillSlot.SECOND),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Text(
                    text = stringResource(R.string.skills_active_title),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            items(
                items = state.activeSkills,
                key = { skill -> skill.skillId }
            ) { skill ->
                SkillCard(
                    skill = skill,
                    isActionInProgress = state.isSkillActionInProgress,
                    onUnlockClick = {
                        onAction(SkillAction.OnUnlockClick(skill.skillId))
                    },
                    onUpgradeClick = {
                        onAction(SkillAction.OnUpgradeClick(skill.skillId))
                    },
                    onEquipClick = { slot ->
                        onAction(
                            SkillAction.OnEquipClick(
                                skillId = skill.skillId,
                                slot = slot
                            )
                        )
                    },
                    onUnequipClick = {
                        skill.equippedSlot?.let { slot ->
                            onAction(SkillAction.OnUnequipClick(slot))
                        }
                    }
                )
            }

            item {
                Text(
                    text = stringResource(R.string.skills_passive_title),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(
                items = state.passiveSkills,
                key = { skill -> skill.skillId }
            ) { skill ->
                SkillCard(
                    skill = skill,
                    isActionInProgress = state.isSkillActionInProgress,
                    onUnlockClick = {
                        onAction(SkillAction.OnUnlockClick(skill.skillId))
                    },
                    onUpgradeClick = {
                        onAction(SkillAction.OnUpgradeClick(skill.skillId))
                    },
                    onEquipClick = { slot ->
                        onAction(
                            SkillAction.OnEquipClick(
                                skillId = skill.skillId,
                                slot = slot
                            )
                        )
                    },
                    onUnequipClick = {
                        skill.equippedSlot?.let { slot ->
                            onAction(SkillAction.OnUnequipClick(slot))
                        }
                    }
                )
            }
        }
    }
}

private fun SkillUiState.equippedSkill(
    slot: SkillSlot
): SkillUiModel? {
    return (activeSkills + passiveSkills).firstOrNull { skill ->
        skill.equippedSlot == slot
    }
}