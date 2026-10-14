package ru.landilf.hellofbullets.presentation.common.window

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun HideDialogSystemBars() {
    val view = LocalView.current

    SideEffect {
        val dialogWindow = (view.parent as? DialogWindowProvider)?.window
            ?: return@SideEffect

        WindowCompat.getInsetsController(
            dialogWindow,
            dialogWindow.decorView
        ).apply {
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            hide(WindowInsetsCompat.Type.systemBars())
        }
    }
}