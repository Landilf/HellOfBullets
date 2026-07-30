package ru.landilf.hellofbullets.presentation.survival.game.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.landilf.hellofbullets.R
import ru.landilf.hellofbullets.presentation.common.formatter.formatElapsedTime

@Composable
fun SurvivalGameHud(
    elapsedTimeMs: Int,
    onPauseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = 16.dp,
                vertical = 16.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(
            onClick = onPauseClick,
            modifier = Modifier.background(
                color = Color(0xAA000000),
                shape = RoundedCornerShape(12.dp)
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.Pause,
                contentDescription = stringResource(R.string.pause_title),
                tint = Color.White
            )
        }

        Box(
            modifier = Modifier
                .background(
                    color = Color(0xAA000000),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                )
        ) {
            Text(
                text = formatElapsedTime(elapsedTimeMs),
                color = Color.White
            )
        }
    }
}