package ru.landilf.hellofbullets.presentation.survival.game

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import ru.landilf.hellofbullets.R
import ru.landilf.hellofbullets.domain.model.battle.survival.SurvivalGameState
import ru.landilf.hellofbullets.domain.model.common.Vector2
import ru.landilf.hellofbullets.presentation.survival.game.component.SurvivalGameCanvas

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
                    Text(text = stringResource(R.string.loading_title))
                }
            }

            state.errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.errorMessage)
                }
            }

            state.gameState != null -> {
                SurvivalGameContent(
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
                    Text(text = "Игра недоступна")
                }
            }
        }
    }
}

@Composable
private fun SurvivalGameContent(
    gameState: SurvivalGameState,
    gameFieldSize: IntSize,
    onAction: (SurvivalGameAction) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101820))
            .pointerInput(gameFieldSize) {
                detectDragGestures { change, dragAmount ->
                    change.consume()

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

        Button(
            onClick = { onAction(SurvivalGameAction.OnBackClick) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text(text = stringResource(R.string.button_back))
        }
    }
}