package ru.landilf.hellofbullets.presentation.survival.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.landilf.hellofbullets.R
import ru.landilf.hellofbullets.domain.model.leaderboard.LeaderboardRecord
import ru.landilf.hellofbullets.presentation.common.CenteredMessage
import ru.landilf.hellofbullets.presentation.common.formatter.formatElapsedTime

@Composable
fun SurvivalLeaderboardScreen(
    state: SurvivalLeaderboardUiState,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101820))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.show_records_button),
            color = Color.White
        )

        when {
            state.isLoading -> {
                CenteredMessage(
                    message = stringResource(R.string.loading_title),
                    modifier = Modifier.weight(1f),
                    color = Color.White
                )
            }

            state.errorMessage != null -> {
                CenteredMessage(
                    message = state.errorMessage,
                    modifier = Modifier.weight(1f),
                    color = Color.Red
                )
            }

            state.records.isEmpty() -> {
                CenteredMessage(
                    message = stringResource(R.string.leaderboard_empty),
                    modifier = Modifier.weight(1f),
                    color = Color.White
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    itemsIndexed(
                        items = state.records,
                        key = { _, record -> record.id }
                    ) { index, record ->
                        LeaderboardRecordRow(
                            position = index + 1,
                            record = record,
                            isCurrentPlayer = record.id == state.playerId
                        )
                    }
                }
            }
        }

        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.back_button))
        }
    }
}

@Composable
private fun LeaderboardRecordRow(
    position: Int,
    record: LeaderboardRecord,
    isCurrentPlayer: Boolean
) {
    val backgroundColor = if (isCurrentPlayer) {
        Color(0xFF244B5A)
    } else {
        Color(0xFF16212B)
    }

    val contentColor = if (isCurrentPlayer) {
        Color(0xFF7CFFB2)
    } else {
        Color.White
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.leaderboard_rank, position),
            color = Color.White
        )

        Text(
            text = record.playerName,
            color = contentColor
        )

        Text(
            text = stringResource(
                R.string.leaderboard_time,
                formatElapsedTime(record.time * 1_000)
            ),
            color = contentColor
        )
    }
}