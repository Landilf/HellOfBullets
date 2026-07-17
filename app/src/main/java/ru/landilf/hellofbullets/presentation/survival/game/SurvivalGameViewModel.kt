package ru.landilf.hellofbullets.presentation.survival.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.landilf.hellofbullets.domain.model.battle.survival.SurvivalPhase
import ru.landilf.hellofbullets.domain.model.common.GameFieldSize
import ru.landilf.hellofbullets.domain.model.common.Vector2
import ru.landilf.hellofbullets.domain.usecase.CreateDefaultSurvivalGameStateUseCase
import ru.landilf.hellofbullets.domain.usecase.UpdateSurvivalGameStateUseCase
import javax.inject.Inject
import kotlin.time.TimeSource

@HiltViewModel
class SurvivalGameViewModel @Inject constructor(
    private val createDefaultSurvivalGameStateUseCase: CreateDefaultSurvivalGameStateUseCase,
    private val updateSurvivalGameStateUseCase: UpdateSurvivalGameStateUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SurvivalGameUiState())
    val uiState: StateFlow<SurvivalGameUiState> = _uiState.asStateFlow()

    private var gameLoopJob: Job? = null

    init {
        startGameLoop()
    }

    fun onAction(action: SurvivalGameAction) {
        when (action) {
            SurvivalGameAction.OnBackClick -> Unit

            is SurvivalGameAction.OnPlayerDrag -> {
                movePlayer(action.dragDelta)
            }

            is SurvivalGameAction.OnGameFieldSizeChange -> {
                updateGameFieldSize(
                    widthPx = action.widthPx,
                    heightPx = action.heightPx
                )
            }
        }
    }

    private fun startGameLoop() {
        gameLoopJob?.cancel()

        gameLoopJob = viewModelScope.launch {
            var previousTimeMark = TimeSource.Monotonic.markNow()

            while (isActive) {
                delay(FRAME_DELAY_MS)

                val elapsedTimeMs = previousTimeMark.elapsedNow().inWholeMilliseconds
                previousTimeMark = TimeSource.Monotonic.markNow()

                val safeDeltaTimeMs = elapsedTimeMs
                    .coerceAtLeast(1L)
                    .coerceAtMost(MAX_DELTA_TIME_MS)
                    .toInt()

                _uiState.update { currentState ->
                    val currentGameState = currentState.gameState ?: return@update currentState

                    if (currentGameState.phase != SurvivalPhase.ACTIVE) {
                        return@update currentState
                    }

                    val updateGameState = updateSurvivalGameStateUseCase(
                        gameState = currentGameState,
                        deltaTimeMs = safeDeltaTimeMs
                    )

                    currentState.copy(
                        gameState = updateGameState
                    )
                }
            }
        }
    }

    private fun movePlayer(
        dragDelta: Vector2
    ) {
        _uiState.update { currentState ->
            val currentGameState = currentState.gameState ?: return@update currentState
            val currentPlayerState = currentGameState.playerRuntimeState
            val fieldSize = currentGameState.fieldSize

            val updatedPosition = Vector2(
                x = (currentPlayerState.position.x + dragDelta.x).coerceIn(
                    minimumValue = currentPlayerState.hitRadius,
                    maximumValue = fieldSize.width - currentPlayerState.hitRadius
                ),
                y = (currentPlayerState.position.y + dragDelta.y).coerceIn(
                    minimumValue = currentPlayerState.hitRadius,
                    maximumValue = fieldSize.height - currentPlayerState.hitRadius
                )
            )

            currentState.copy(
                gameState = currentGameState.copy(
                    playerRuntimeState = currentPlayerState.copy(
                        position = updatedPosition
                    )
                )
            )
        }
    }

    private fun updateGameFieldSize(
        widthPx: Int,
        heightPx: Int
    ) {
        if (widthPx <= 0 || heightPx <= 0) {
            return
        }

        val updatedFieldSize = createGameFieldSize(
            widthPx = widthPx,
            heightPx = heightPx
        )

        if (_uiState.value.gameState == null) {
            loadInitialState(updatedFieldSize)
        }
    }

    private fun createGameFieldSize(
        widthPx: Int,
        heightPx: Int
    ): GameFieldSize {
        val worldWidth = 100f
        val worldHeight = worldWidth * heightPx.toFloat() / widthPx.toFloat()

        return GameFieldSize(
            width = worldWidth,
            height = worldHeight
        )
    }

    private fun loadInitialState(
        fieldSize: GameFieldSize
    ) {
        val initialGameState = createDefaultSurvivalGameStateUseCase(fieldSize)

        _uiState.value = SurvivalGameUiState(
            isLoading = false,
            gameState = initialGameState,
            errorMessage = null
        )
    }

    private companion object {
        const val FRAME_DELAY_MS = 16L
        const val MAX_DELTA_TIME_MS = 100L
    }
}