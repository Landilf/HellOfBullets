package ru.landilf.hellofbullets.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class AppNavigationItem(
    val destination: AppDestination,
    @field:StringRes val labelRes: Int,
    val icon: ImageVector
)
