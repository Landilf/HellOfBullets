package ru.landilf.hellofbullets.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import ru.landilf.hellofbullets.presentation.mainmenu.MainMenuViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainMenuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                AppRoot(
                    mainMenuState = viewModel.uiState.collectAsStateWithLifecycle().value,
                    onExit = { finish() }
                )
            }
        }
    }
}
