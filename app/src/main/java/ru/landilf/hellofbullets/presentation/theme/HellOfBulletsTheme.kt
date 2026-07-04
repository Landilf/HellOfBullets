package ru.landilf.hellofbullets.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun HellOfBulletsTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        content = content
    )
}