package ru.landilf.hellofbullets.presentation.survival.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.landilf.hellofbullets.R

@Composable
fun SurvivalGameScreen(
    state: SurvivalGameUiState,
    onAction: (SurvivalGameAction) -> Unit
) {
    when {
        state.isLoading -> {
            SurvivalGameContent(
                title = stringResource(R.string.loading_title),
                body = stringResource(R.string.loading_message),
                onBackClick = {
                    onAction(SurvivalGameAction.OnBackClick)
                }
            )
        }

        state.errorMessage != null -> {
            SurvivalGameContent(
                title = stringResource(R.string.error_title),
                body = state.errorMessage,
                onBackClick = {
                    onAction(SurvivalGameAction.OnBackClick)
                }
            )
        }

        state.gameState != null -> {
            val gameState = state.gameState

            SurvivalGameContent(
                title = stringResource(R.string.survival_title),
                body = buildString {
                    appendLine("Фаза: ${gameState.phase}")
                    appendLine("Время: ${gameState.elapsedTimeMs} мс")
                    appendLine("Здоровье: ${gameState.playerRuntimeState.currentHp}")
                    appendLine("Игрок живой: ${gameState.playerRuntimeState.isAlive}")
                    appendLine("Позиция игрока: ${gameState.playerRuntimeState.position}")
                    appendLine("Снарядов: ${gameState.activeProjectiles.size}")
                    appendLine("ID шаблона атаки: ${gameState.survivalWaveState?.currentPatternIndex}")
                    appendLine("Время до следующего залпа: ${gameState.survivalWaveState?.timeUntilNextVolleyMs}")
                },
                onBackClick = {
                    onAction(SurvivalGameAction.OnBackClick)
                }
            )
        }

        else -> {
            SurvivalGameContent(
                title = stringResource(R.string.survival_title),
                body = "Игра недоступна",
                onBackClick = {
                    onAction(SurvivalGameAction.OnBackClick)
                }
            )
        }
    }
}

@Composable
private fun SurvivalGameContent(
    title: String,
    body: String,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = title)
        Text(text = body)
        Button(onClick = onBackClick) {
            Text(text = stringResource(R.string.button_back))
        }
    }
}