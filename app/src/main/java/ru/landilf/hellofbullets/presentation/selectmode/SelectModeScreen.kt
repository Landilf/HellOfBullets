package ru.landilf.hellofbullets.presentation.selectmode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.landilf.hellofbullets.R

@Composable
fun SelectModeScreen(
    onSurvivalClick: () -> Unit,
    onDuelClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.select_mode_title))
        Text(stringResource(R.string.page_in_development))

        Button(
            onClick = onSurvivalClick,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(stringResource(R.string.select_mode_survival))
        }

        Button(
            onClick = onDuelClick,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(stringResource(R.string.select_mode_duel))
        }

        Button(
            onClick = onBackClick,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(stringResource(R.string.back_button))
        }
    }
}