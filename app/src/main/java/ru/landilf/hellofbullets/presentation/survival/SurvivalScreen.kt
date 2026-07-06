package ru.landilf.hellofbullets.presentation.survival

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
fun SurvivalScreen(
    onStartGameClick: () -> Unit,
    onShowRecordsClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(id = R.string.select_mode_survival))
        Text(text = stringResource(id = R.string.page_in_development))

        Button(
            modifier = Modifier.padding(24.dp),
            onClick = onStartGameClick
        ) {
            Text(text = stringResource(id = R.string.button_start_game))
        }

        Button(
            modifier = Modifier.padding(24.dp),
            onClick = onShowRecordsClick
        ) {
            Text(text = stringResource(id = R.string.button_show_records))
        }

        Button(
            modifier = Modifier.padding(24.dp),
            onClick = onBackClick
        ) {
            Text(text = stringResource(id = R.string.button_back))
        }
    }
}