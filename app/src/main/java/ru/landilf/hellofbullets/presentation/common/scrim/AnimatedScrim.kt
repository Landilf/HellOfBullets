package ru.landilf.hellofbullets.presentation.common.scrim

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun AnimatedScrim(
    isVisible: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
    maxAlpha: Float = 0.56f
) {
    require(maxAlpha in 0f..1f) {
        "Прозрачность затемнения должна быть в диапазоне от 0 до 1"
    }

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) maxAlpha else 0f,
        animationSpec = tween(durationMillis = 180),
        label = "scrimAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color.copy(alpha = alpha))
            .then(
                onClick?.let { click ->
                    if (isVisible) {
                        Modifier.clickable(
                            interactionSource = null,
                            indication = null,
                            onClick = click
                        )
                    } else {
                        Modifier
                    }
                } ?: Modifier
            )
    )
}