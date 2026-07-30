package ru.landilf.hellofbullets.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import ru.landilf.hellofbullets.domain.engine.player.PlayerProgressionCalculator
import ru.landilf.hellofbullets.domain.usecase.player.GetOrCreatePlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.player.ObservePlayerStateUseCase
import javax.inject.Inject

@HiltViewModel
class PlayerSummaryViewModel @Inject constructor(
    private val getOrCreatePlayerStateUseCase: GetOrCreatePlayerStateUseCase,
    private val observePlayerStateUseCase: ObservePlayerStateUseCase,
    private val playerProgressionCalculator: PlayerProgressionCalculator
) : ViewModel() {
    private val _uiState = MutableStateFlow(PlayerSummaryUiState())
    val uiState: StateFlow<PlayerSummaryUiState> = _uiState.asStateFlow()

    init {
        observePlayerSummary()
    }

    private fun observePlayerSummary() {
        viewModelScope.launch {
            try {
                getOrCreatePlayerStateUseCase()

                observePlayerStateUseCase()
                    .filterNotNull()
                    .collect { playerState ->
                        val profile = playerState.playerProfile
                        val experienceProgress = playerProgressionCalculator.calculateProgress(
                            totalExperience = profile.totalExperience
                        )

                        _uiState.value = PlayerSummaryUiState(
                            isLoading = false,
                            playerName = profile.name,
                            level = experienceProgress.level,
                            experienceInCurrentLevel = experienceProgress.experienceInCurrentLevel,
                            requiredExperienceForNextLevel = experienceProgress.requiredExperienceForNextLevel,
                            silverAmount = profile.silverAmount
                        )
                    }
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                _uiState.value = PlayerSummaryUiState(
                    isLoading = false
                )
            }
        }
    }
}