package ru.landilf.hellofbullets.presentation.skills

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.landilf.hellofbullets.domain.model.player.PlayerState
import ru.landilf.hellofbullets.domain.model.skills.SkillSlot
import ru.landilf.hellofbullets.domain.model.skills.definition.PassiveSkillDefinition
import ru.landilf.hellofbullets.domain.model.skills.definition.SkillDefinition
import ru.landilf.hellofbullets.domain.usecase.player.GetOrCreatePlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.player.ObservePlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.skills.GetSkillDefinitionsUseCase
import ru.landilf.hellofbullets.domain.usecase.skills.SetEquippedSkillUseCase
import ru.landilf.hellofbullets.domain.usecase.skills.UnlockPlayerSkillUseCase
import ru.landilf.hellofbullets.domain.usecase.skills.UpgradePlayerSkillUseCase
import javax.inject.Inject

@HiltViewModel
class SkillsViewModel @Inject constructor(
    private val getOrCreatePlayerStateUseCase: GetOrCreatePlayerStateUseCase,
    private val observePlayerStateUseCase: ObservePlayerStateUseCase,
    private val getSkillDefinitionsUseCase: GetSkillDefinitionsUseCase,
    private val unlockPlayerSkillUseCase: UnlockPlayerSkillUseCase,
    private val upgradePlayerSkillUseCase: UpgradePlayerSkillUseCase,
    private val setEquippedSkillUseCase: SetEquippedSkillUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SkillUiState())
    val uiState: StateFlow<SkillUiState> = _uiState.asStateFlow()

    private var definitions: List<SkillDefinition> = emptyList()

    init {
        loadSkills()
    }

    fun onAction(action: SkillAction) {
        when (action) {
            is SkillAction.OnUnlockClick -> {
                unlockSkill(action.skillId)
            }

            is SkillAction.OnUpgradeClick -> {
                upgradeSkill(action.skillId)
            }

            is SkillAction.OnEquipClick -> {
                setEquippedSkill(
                    slot = action.slot,
                    skillId = action.skillId
                )
            }

            is SkillAction.OnUnequipClick -> {
                setEquippedSkill(
                    slot = action.slot,
                    skillId = null
                )
            }
        }
    }

    private fun loadSkills() {
        viewModelScope.launch {
            try {
                definitions = getSkillDefinitionsUseCase()
                getOrCreatePlayerStateUseCase()

                observePlayerStateUseCase()
                    .filterNotNull()
                    .collect(::updateUiState)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = exception.message
                            ?: "Не удалось загрузить навыки"
                    )
                }
            }
        }
    }

    private fun updateUiState(
        playerState: PlayerState
    ) {
        val progressesBySkillId = playerState.skillProgresses.associateBy { progress ->
            progress.skillId
        }
        val equippedSlotsBySkillId = buildMap {
            playerState.playerBuild.firstSkillId?.let { skillId ->
                put(skillId, SkillSlot.FIRST)
            }
            playerState.playerBuild.secondSkillId?.let { skillId ->
                put(skillId, SkillSlot.SECOND)
            }
        }

        val activeSkills = definitions
            .filterNot { definition ->
                definition is PassiveSkillDefinition
            }
            .map { definition ->
                definition.toUiModel(
                    progress = progressesBySkillId[definition.id],
                    equippedSlot = equippedSlotsBySkillId[definition.id]
                )
            }

        val passiveSkills = definitions
            .filterIsInstance<PassiveSkillDefinition>()
            .map { definition ->
                definition.toUiModel(
                    progress = progressesBySkillId[definition.id],
                    equippedSlot = equippedSlotsBySkillId[definition.id]
                )
            }

        _uiState.update { state ->
            state.copy(
                isLoading = false,
                skillPointAmount = playerState.playerProfile.skillPointAmount,
                activeSkills = activeSkills,
                passiveSkills = passiveSkills,
                errorMessage = null
            )
        }
    }

    private fun unlockSkill(skillId: Long) {
        performSkillAction(
            fallbackErrorMessage = "Не удалось разблокировать навык"
        ) {
            unlockPlayerSkillUseCase(skillId)
        }
    }

    private fun upgradeSkill(skillId: Long) {
        performSkillAction(
            fallbackErrorMessage = "Не удалось улучшить навык"
        ) {
            upgradePlayerSkillUseCase(skillId)
        }
    }

    private fun setEquippedSkill(
        slot: SkillSlot,
        skillId: Long?
    ) {
        performSkillAction(
            fallbackErrorMessage = "Не удалось изменить сборку навыков"
        ) {
            setEquippedSkillUseCase(
                slot = slot,
                skillId = skillId
            )
        }
    }

    private fun performSkillAction(
        fallbackErrorMessage: String,
        action: suspend () -> Unit
    ) {
        if (_uiState.value.isSkillActionInProgress) {
            return
        }

        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    isSkillActionInProgress = true,
                    errorMessage = null
                )
            }

            try {
                action()
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update { state ->
                    state.copy(
                        errorMessage = exception.message ?: fallbackErrorMessage
                    )
                }
            } finally {
                _uiState.update { state ->
                    state.copy(isSkillActionInProgress = false)
                }
            }
        }
    }
}