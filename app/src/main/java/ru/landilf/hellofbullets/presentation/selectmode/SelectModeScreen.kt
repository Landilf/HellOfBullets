package ru.landilf.hellofbullets.presentation.selectmode

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import ru.landilf.hellofbullets.R

private data class ModeSelectionItem(
    @field:StringRes val titleRes: Int,
    @field:StringRes val descriptionRes: Int,
    val onPlayClick: () -> Unit
)

@Composable
fun SelectModeScreen(
    onSurvivalClick: () -> Unit,
    onDuelClick: () -> Unit,
) {
    val modes = listOf(
        ModeSelectionItem(
            titleRes = R.string.select_mode_survival,
            descriptionRes = R.string.select_mode_survival_description,
            onPlayClick = onSurvivalClick
        ),
        ModeSelectionItem(
            titleRes = R.string.select_mode_duel,
            descriptionRes = R.string.select_mode_duel_description,
            onPlayClick = onDuelClick
        )
    )

    val pagerState = rememberPagerState(
        pageCount = { modes.size }
    )
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 24.dp,
                vertical = 16.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.select_mode_title))

        val canGoToPreviousMode = pagerState.currentPage > 0
        val canGoToNextMode = pagerState.currentPage < modes.lastIndex

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ModeBanner(
                        mode = modes[page]
                    )
                }
            }

            IconButton(
                enabled = canGoToPreviousMode,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(
                            page = pagerState.currentPage - 1
                        )
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = (-16).dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = stringResource(R.string.select_mode_previous)
                )
            }

            IconButton(
                enabled = canGoToNextMode,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(
                            page = pagerState.currentPage + 1
                        )
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = stringResource(R.string.select_mode_next)
                )
            }
        }
    }
}

@Composable
private fun ModeBanner(
    mode: ModeSelectionItem
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.72f)
            .fillMaxHeight(0.6f)
            .clickable(onClick = mode.onPlayClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(mode.titleRes),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = stringResource(mode.descriptionRes),
                textAlign = TextAlign.Center
            )

            Text(
                stringResource(R.string.select_mode_start_hint),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
            )
        }
    }
}