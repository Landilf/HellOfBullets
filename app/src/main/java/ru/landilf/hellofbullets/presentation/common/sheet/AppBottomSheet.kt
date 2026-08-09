package ru.landilf.hellofbullets.presentation.common.sheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import ru.landilf.hellofbullets.presentation.common.scrim.AnimatedScrim
import kotlin.math.roundToInt

@Composable
fun AppBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    heightFraction: Float = 0.8f,
    content: @Composable () -> Unit
) {
    require(heightFraction in 0f..1f) {
        "Высота нижней панели должна быть в диапазоне от 0 до 1"
    }

    val visibilityState = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }

    val dragInteractionSource = remember { MutableInteractionSource() }
    val isDragging by dragInteractionSource.collectIsDraggedAsState()
    var isClosing by remember { mutableStateOf(false) }
    var isScrimVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isScrimVisible = true
    }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }
    var sheetHeightPx by remember { mutableIntStateOf(0) }

    val animatedDragOffsetY by animateFloatAsState(
        targetValue = dragOffsetY,
        animationSpec = if (isDragging) {
            snap()
        } else {
            spring()
        },
        label = "bottomSheetDragOffset"
    )

    fun dismiss() {
        if (!isClosing) {
            isClosing = true
            isScrimVisible = false
            visibilityState.targetState = false
        }
    }

    LaunchedEffect(
        isClosing,
        visibilityState.currentState,
        visibilityState.isIdle
    ) {
        if (isClosing && visibilityState.isIdle && !visibilityState.currentState) {
            onDismissRequest()
        }
    }

    val dragState = rememberDraggableState { delta ->
        dragOffsetY = (dragOffsetY + delta).coerceAtLeast(0f)
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        AnimatedScrim(
            isVisible = isScrimVisible,
            onClick = if (!isClosing) {
                ::dismiss
            } else {
                null
            }
        )

        AnimatedVisibility(
            visibleState = visibilityState,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(
                animationSpec = tween(
                    durationMillis = 260,
                    easing = FastOutSlowInEasing
                ),
                initialOffsetY = { fullHeight -> fullHeight }
            ) + fadeIn(
                animationSpec = tween(durationMillis = 180)
            ),
            exit = slideOutVertically(
                animationSpec = tween(
                    durationMillis = 220,
                    easing = FastOutSlowInEasing
                ),
                targetOffsetY = { fullHeight -> fullHeight }
            ) + fadeOut(
                animationSpec = tween(durationMillis = 150)
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(heightFraction)
                    .onSizeChanged { size ->
                        sheetHeightPx = size.height
                    }
                    .offset {
                        IntOffset(
                            x = 0,
                            y = animatedDragOffsetY.roundToInt()
                        )
                    }
                    .draggable(
                        state = dragState,
                        orientation = Orientation.Vertical,
                        enabled = !isClosing,
                        interactionSource = dragInteractionSource,
                        onDragStopped = { velocity ->
                            val shouldDismiss = dragOffsetY >
                                    sheetHeightPx * 0.2f || velocity > 1_000f

                            if (shouldDismiss) {
                                dismiss()
                            } else {
                                dragOffsetY = 0f
                            }
                        }
                    )
                    .then(
                        if (!isClosing) {
                            Modifier.clickable(
                                interactionSource = null,
                                indication = null,
                                onClick = {}
                            )
                        } else {
                            Modifier
                        }
                    ),
                shape = RoundedCornerShape(
                    topStart = 28.dp,
                    topEnd = 28.dp
                ),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Spacer(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(4.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.outline,
                                        shape = RoundedCornerShape(2.dp)
                                    )
                            )
                        }

                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            content()
                        }
                    }

                    if (isClosing) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable(
                                    interactionSource = null,
                                    indication = null,
                                    onClick = {}
                                )
                        )
                    }
                }
            }
        }
    }
}