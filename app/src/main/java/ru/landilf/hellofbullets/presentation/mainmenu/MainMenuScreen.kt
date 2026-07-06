package ru.landilf.hellofbullets.presentation.mainmenu

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
fun MainMenuScreen(
    state: MainMenuUiState,
    onClickAction: (MainMenuAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(id = state.appName))
        Text(text = stringResource(id = state.title))

        Button(
            onClick = { onClickAction(MainMenuAction.SelectMode) },
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text(text = stringResource(id = R.string.main_menu_select_mode))
        }

        Button(
            onClick = { onClickAction(MainMenuAction.Skills) },
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text(text = stringResource(id = R.string.main_menu_skills))
        }

        Button(
            onClick = { onClickAction(MainMenuAction.Equipment) },
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text(text = stringResource(id = R.string.main_menu_equipment))
        }

        Button(
            onClick = { onClickAction(MainMenuAction.Shop) },
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text(text = stringResource(id = R.string.main_menu_shop))
        }

        Button(
            onClick = { onClickAction(MainMenuAction.Settings) },
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text(text = stringResource(id = R.string.main_menu_settings))
        }

        Button(
            onClick = { onClickAction(MainMenuAction.Exit) },
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text(text = stringResource(id = R.string.main_menu_exit))
        }
    }
}