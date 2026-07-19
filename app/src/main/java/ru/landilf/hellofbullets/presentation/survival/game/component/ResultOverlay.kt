package ru.landilf.hellofbullets.presentation.survival.game.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.landilf.hellofbullets.R
import ru.landilf.hellofbullets.presentation.common.formatter.formatElapsedTime
import ru.landilf.hellofbullets.presentation.survival.game.SurvivalResultUiState

@Composable
fun ResultOverlay(
    result: SurvivalResultUiState,
    onRestartClick: () -> Unit,
    onExitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OverlayCard(
        title = stringResource(R.string.result_title),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 84.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 24.dp,
                alignment = Alignment.CenterVertically
            )
        ) {
            Text(
                text = stringResource(
                    R.string.result_time_value,
                    formatElapsedTime(result.elapsedTimeMs)
                ),
                color = Color.White
            )

            Text(
                text = stringResource(
                    R.string.result_exp_value, result.reward.exp
                ),
                color = Color.White
            )

            Text(
                text = stringResource(
                    R.string.result_silver_value, result.reward.silver
                ),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onRestartClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.game_restart_button))
            }

            Button(
                onClick = onExitClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.game_exit_button))
            }
        }

    }
}