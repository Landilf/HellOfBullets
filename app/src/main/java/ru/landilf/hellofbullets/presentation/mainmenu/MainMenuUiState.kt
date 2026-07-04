package ru.landilf.hellofbullets.presentation.mainmenu

import androidx.annotation.StringRes
import ru.landilf.hellofbullets.R

data class MainMenuUiState(
    @param:StringRes val title: Int = R.string.main_menu_title,
    @param:StringRes val subtitle: Int = R.string.main_menu_subtitle
)
