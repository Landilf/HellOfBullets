package ru.landilf.hellofbullets.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import ru.landilf.hellofbullets.presentation.mainmenu.MainMenuAction
import ru.landilf.hellofbullets.presentation.mainmenu.MainMenuViewModel
import ru.landilf.hellofbullets.presentation.mainmenu.MainScreen
import ru.landilf.hellofbullets.presentation.theme.HellOfBulletsTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainMenuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HellOfBulletsTheme {
                val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
                MainScreen(
                    state = uiState,
                    onClickAction = { action ->
                        when (action) {
                            MainMenuAction.SelectMode -> showToast("Переход к выбору режима")
                            MainMenuAction.Skills -> showToast("Экран навыков в разработке")
                            MainMenuAction.Equipment -> showToast("Экран снаряжения в разработке")
                            MainMenuAction.Shop -> showToast("Магазин в разработке")
                            MainMenuAction.Settings -> showToast("Экран настроек в разработке")
                            MainMenuAction.Exit -> finish()
                        }
                    }
                )
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
