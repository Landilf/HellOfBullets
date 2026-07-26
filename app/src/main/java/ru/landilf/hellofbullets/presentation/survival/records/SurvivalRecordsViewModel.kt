package ru.landilf.hellofbullets.presentation.survival.records

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.landilf.hellofbullets.domain.usecase.GetLeaderboardUseCase
import ru.landilf.hellofbullets.domain.usecase.GetOrCreatePlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.InitializeLocalLeaderboardUseCase
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class SurvivalRecordsViewModel @Inject constructor(
    private val getOrCreatePlayerStateUseCase: GetOrCreatePlayerStateUseCase,
    private val initializeLocalLeaderboardUseCase: InitializeLocalLeaderboardUseCase,
    private val getLeaderboardUseCase: GetLeaderboardUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SurvivalRecordsUiState())
    val uiState: StateFlow<SurvivalRecordsUiState> = _uiState.asStateFlow()

    init {
        loadRecords()
    }

    private fun loadRecords() {
        viewModelScope.launch {
            try {
                initializeLocalLeaderboardUseCase()

                val playerState = getOrCreatePlayerStateUseCase()
                val records = getLeaderboardUseCase()

                _uiState.value = SurvivalRecordsUiState(
                    isLoading = false,
                    records = records,
                    playerName = playerState.playerProfile.name
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = SurvivalRecordsUiState(
                    isLoading = false,
                    errorMessage = exception.message ?: "Не удалось загрузить таблицу рекордов"
                )
            }
        }
    }
}