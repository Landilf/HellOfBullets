package ru.landilf.hellofbullets.presentation.survival.game.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
        modifier = modifier,
        cardHeightFraction = 0.7f
    ) {
        OverlayContentColumn(
            spacing = 16.dp,
            modifier = Modifier.weight(1f)
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(
                    R.string.result_time_value,
                    formatElapsedTime(result.elapsedTimeMs)
                ),
                color = Color.White
            )

            if (result.isNewRecord) {
                Text(
                    text = stringResource(R.string.result_new_record),
                    color = Color(0xFFFFD166)
                )
            }

            if (result.leaderboardPosition != null) {
                Text(
                    text = stringResource(
                        R.string.result_leaderboard_position,
                        result.leaderboardPosition
                    ),
                    color = Color.White
                )
            } else {
                result.leaderboardCutoffTime?.let { cutoffTime ->
                    Text(
                        text = stringResource(
                            R.string.result_leaderboard_cutoff,
                            formatElapsedTime(cutoffTime * 1_000)
                        ),
                        color = Color.White
                    )
                }
            }

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

            Spacer(modifier = Modifier.weight(1f))

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