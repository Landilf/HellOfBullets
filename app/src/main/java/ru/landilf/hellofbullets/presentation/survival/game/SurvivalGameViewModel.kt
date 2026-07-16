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
        loadInitialState()
        startGameLoop()
    }

    fun onAction(action: SurvivalGameAction) {
        when (action) {
            SurvivalGameAction.OnBackClick -> Unit

            is SurvivalGameAction.OnPlayerDrag -> {
                movePlayer(action.dragDelta)
            }
        }
    }

    private fun loadInitialState() {
        val initialGameState = createInitialSurvivalGameStateUseCase(
            playerStats = createInitialPlayerState(),
            initialWaveState = createInitialWaveState()
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
            projectileCount = 3,
            spawnIntervalMs = 1000,
            projectileSpeed = 0.25f,
            projectileDamage = 1,
            projectileHitRadius = 0.03f,
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
                    deltaTimeMs = FRAME_DELAY_MS.toInt()
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

        val updatedPosition = Vector2(
            x = (currentGameState.playerRuntimeState.position.x + dragDelta.x).coerceIn(0f, 1f),
            y = (currentGameState.playerRuntimeState.position.y + dragDelta.y).coerceIn(0f, 1f)
        )

        _uiState.value = _uiState.value.copy(
            gameState = currentGameState.copy(
                playerRuntimeState = currentPlayerState.copy(
                    position = updatedPosition
                )
            )
        )
    }

    private companion object {
        const val FRAME_DELAY_MS = 16L
    }
}