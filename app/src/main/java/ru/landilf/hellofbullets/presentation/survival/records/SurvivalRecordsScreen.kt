package ru.landilf.hellofbullets.presentation.survival.records

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.landilf.hellofbullets.R
import ru.landilf.hellofbullets.domain.model.leaderboard.LeaderboardRecord
import ru.landilf.hellofbullets.presentation.common.formatter.formatElapsedTime

@Composable
fun SurvivalRecordsScreen(
    state: SurvivalRecordsUiState,
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.loading_title),
                        color = Color.White
                    )
                }
            }

            state.errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.errorMessage,
                        color = Color.Red
                    )
                }
            }

            state.records.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.records_empty),
                        color = Color.White
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    itemsIndexed(
                        items = state.records,
                        key = { _, record -> record.playerName }
                    ) { index, record ->
                        LeaderboardRecordRow(
                            position = index + 1,
                            record = record,
                            isCurrentPlayer = record.playerName == state.playerName
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isCurrentPlayer) {
                    Color(0xFF244B5A)
                } else {
                    Color(0xFF16212B)
                }
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.records_rank, position),
            color = Color.White
        )

        Text(
            text = record.playerName,
            color = if (isCurrentPlayer) Color(0xFF7CFFB2) else Color.White
        )

        Text(
            text = stringResource(
                R.string.records_time,
                formatElapsedTime(record.time * 1_000)
            ),
            color = if (isCurrentPlayer) Color(0xFF7CFFB2) else Color.White
        )
    }
}