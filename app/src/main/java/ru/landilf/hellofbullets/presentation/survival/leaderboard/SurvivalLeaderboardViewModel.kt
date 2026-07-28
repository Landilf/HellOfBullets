package ru.landilf.hellofbullets.presentation.survival.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.landilf.hellofbullets.domain.usecase.leaderboard.GetLeaderboardUseCase
import ru.landilf.hellofbullets.domain.usecase.player.GetOrCreatePlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.leaderboard.SyncSurvivalLeaderboardUseCase
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class SurvivalLeaderboardViewModel @Inject constructor(
    private val getOrCreatePlayerStateUseCase: GetOrCreatePlayerStateUseCase,
    private val getLeaderboardUseCase: GetLeaderboardUseCase,
    private val syncSurvivalLeaderboardUseCase: SyncSurvivalLeaderboardUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SurvivalLeaderboardUiState())
    val uiState: StateFlow<SurvivalLeaderboardUiState> = _uiState.asStateFlow()

    init {
        loadRecords()
    }

    private fun loadRecords() {
        viewModelScope.launch {
            try {
                try {
                    syncSurvivalLeaderboardUseCase()
                } catch (exception: CancellationException) {
                    throw exception
                } catch (_: Exception) {
                    // При отсутствии сети используется локальный кэш Room
                }

                val playerState = getOrCreatePlayerStateUseCase()
                val records = getLeaderboardUseCase()

                _uiState.value = SurvivalLeaderboardUiState(
                    isLoading = false,
                    records = records,
                    playerId = playerState.playerProfile.id.toString()
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = SurvivalLeaderboardUiState(
                    isLoading = false,
                    errorMessage = exception.message ?: "Не удалось загрузить таблицу рекордов"
                )
            }
        }
    }
}