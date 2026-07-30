package ru.landilf.hellofbullets.presentation.survival.game.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.landilf.hellofbullets.R

@Composable
fun PauseMenuOverlay(
    onResumeClick: () -> Unit,
    onRestartClick: () -> Unit,
    onExitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OverlayCard(
        title = stringResource(R.string.pause_title),
        modifier = modifier,
    ) {
        OverlayContentColumn(
            spacing = 32.dp,
            modifier = Modifier.weight(1f)
        ) {
            Button(
                onClick = onResumeClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.game_resume_button)
                )
            }

            Button(
                onClick = onRestartClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.game_restart_button)
                )
            }

            Button(
                onClick = onExitClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.game_exit_button)
                )
            }
        }
    }
}