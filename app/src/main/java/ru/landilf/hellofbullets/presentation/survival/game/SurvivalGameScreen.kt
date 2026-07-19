package ru.landilf.hellofbullets.presentation.survival.game

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import ru.landilf.hellofbullets.R
import ru.landilf.hellofbullets.domain.model.battle.survival.SurvivalGameState
import ru.landilf.hellofbullets.domain.model.battle.survival.SurvivalPhase
import ru.landilf.hellofbullets.domain.model.common.Vector2
import ru.landilf.hellofbullets.presentation.survival.game.component.PauseMenuOverlay
import ru.landilf.hellofbullets.presentation.survival.game.component.ResultOverlay
import ru.landilf.hellofbullets.presentation.survival.game.component.SurvivalGameCanvas
import ru.landilf.hellofbullets.presentation.survival.game.component.SurvivalGameHud

@Composable
fun SurvivalGameScreen(
    state: SurvivalGameUiState,
    onAction: (SurvivalGameAction) -> Unit
) {
    var gameFieldSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged {
                gameFieldSize = it
                onAction(
                    SurvivalGameAction.OnGameFieldSizeChange(
                        widthPx = it.width,
                        heightPx = it.height
                    )
                )
            }
    ) {
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.loading_title))
                }
            }

            state.errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.errorMessage)
                }
            }

            state.gameState != null -> {
                SurvivalGameContent(
                    state = state,
                    gameState = state.gameState,
                    gameFieldSize = gameFieldSize,
                    onAction = onAction
                )
            }

            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.page_game_unavailable))
                }
            }
        }
    }
}

@Composable
private fun SurvivalGameContent(
    state: SurvivalGameUiState,
    gameState: SurvivalGameState,
    gameFieldSize: IntSize,
    onAction: (SurvivalGameAction) -> Unit,
) {
    val isPaused = gameState.phase == SurvivalPhase.PAUSED

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101820))
            .pointerInput(gameFieldSize) {
                detectDragGestures { change, dragAmount ->
                    change.consume()

                    if (isPaused) {
                        return@detectDragGestures
                    }

                    if (gameFieldSize.width == 0 || gameFieldSize.height == 0) {
                        return@detectDragGestures
                    }

                    val worldDelta = Vector2(
                        x = dragAmount.x / gameFieldSize.width * gameState.fieldSize.width,
                        y = dragAmount.y / gameFieldSize.height * gameState.fieldSize.height
                    )

                    onAction(SurvivalGameAction.OnPlayerDrag(worldDelta))
                }
            }
    ) {
        SurvivalGameCanvas(
            gameState = gameState,
            modifier = Modifier.fillMaxSize()
        )

        SurvivalGameHud(
            elapsedTimeMs = gameState.elapsedTimeMs,
            onPausedClick = { onAction(SurvivalGameAction.OnPauseClick) },
            modifier = Modifier.align(Alignment.TopCenter)
        )

        if (isPaused) {
            PauseMenuOverlay(
                onResumeClick = { onAction(SurvivalGameAction.OnResumeClick) },
                onRestartClick = { onAction(SurvivalGameAction.OnRestartClick) },
                onExitClick = { onAction(SurvivalGameAction.OnExitClick) }
            )
        }

        if (state.isResultVisible && state.result != null) {
            ResultOverlay(
                result = state.result,
                onRestartClick = { onAction(SurvivalGameAction.OnRestartClick) },
                onExitClick = { onAction(SurvivalGameAction.OnExitClick) }
            )
        }
    }
}