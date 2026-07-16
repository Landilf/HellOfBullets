package ru.landilf.hellofbullets.presentation.survival.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.AttackPattern
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.AttackWave
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.ProjectileType
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.SpawnZone
import ru.landilf.hellofbullets.domain.model.battle.survival.SurvivalPhase
import ru.landilf.hellofbullets.domain.model.battle.survival.SurvivalWaveState
import ru.landilf.hellofbullets.domain.model.common.GameFieldSize
import ru.landilf.hellofbullets.domain.model.common.Vector2
import ru.landilf.hellofbullets.domain.model.player.PlayerStats
import ru.landilf.hellofbullets.domain.usecase.CreateInitialSurvivalGameStateUseCase
import ru.landilf.hellofbullets.domain.usecase.UpdateSurvivalGameStateUseCase
import javax.inject.Inject

@HiltViewModel
class SurvivalGameViewModel @Inject constructor(
    private val createInitialSurvivalGameStateUseCase: CreateInitialSurvivalGameStateUseCase,
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
            while (true) {
                delay(FRAME_DELAY_MS)

                val currentGameState = _uiState.value.gameState ?: continue

                if (currentGameState.phase != SurvivalPhase.ACTIVE) {
                    continue
                }

                val updateGameState = updateSurvivalGameStateUseCase(
                    gameState = currentGameState,
                    deltaTimeMs = FRAME_DELAY_MS.toInt(),
                    fieldSize = currentGameState.fieldSize
                )

                _uiState.value = _uiState.value.copy(
                    gameState = updateGameState
                )
            }
        }
    }

    private fun movePlayer(
        dragDelta: Vector2
    ) {
        val currentGameState = _uiState.value.gameState ?: return
        val currentPlayerState = currentGameState.playerRuntimeState
        val fieldSize = currentGameState.fieldSize

        val updatedPosition = Vector2(
            x = (currentPlayerState.position.x + dragDelta.x).coerceIn(
                minimumValue = 0f,
                maximumValue = fieldSize.width
            ),
            y = (currentPlayerState.position.y + dragDelta.y).coerceIn(
                minimumValue = 0f,
                maximumValue = fieldSize.height
            )
        )

        _uiState.value = _uiState.value.copy(
            gameState = currentGameState.copy(
                playerRuntimeState = currentPlayerState.copy(
                    position = updatedPosition
                )
            )
        )
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
        val initialGameState = createInitialSurvivalGameStateUseCase(
            playerStats = createInitialPlayerState(),
            initialWaveState = createInitialWaveState(),
            fieldSize = fieldSize
        )

        _uiState.value = SurvivalGameUiState(
            isLoading = false,
            gameState = initialGameState,
            errorMessage = null
        )
    }

    private fun createInitialPlayerState(): PlayerStats {
        return PlayerStats(
            maxHp = 1,
            damage = 0,
            defense = 0,
            cooldownReduction = 0,
            effectDurationBonus = 0
        )
    }

    private fun createInitialWaveState(): SurvivalWaveState {
        val initialPattern = AttackPattern(
            id = 1L,
            projectileType = ProjectileType.BULLET,
            spawnZone = SpawnZone.TOP,
            projectileCount = 4,
            spawnIntervalMs = 500,
            projectileSpeed = 75f,
            projectileDamage = 1,
            projectileHitRadius = 2f,
            projectileLifetimeMs = 5000
        )

        val initialWave = AttackWave(
            id = 1L,
            patterns = listOf(initialPattern),
            durationMs = 30_000,
            breakAfterMs = 2_000
        )

        return SurvivalWaveState(
            currentWave = initialWave,
            currentPatternIndex = 0,
            elapsedWaveTimeMs = 0,
            timeUntilNextVolleyMs = initialPattern.spawnIntervalMs
        )
    }

    private companion object {
        const val FRAME_DELAY_MS = 16L
    }
}