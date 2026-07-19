package ru.landilf.hellofbullets.presentation.common.formatter

fun formatElapsedTime(
    elapsedTimeMs: Int
): String {
    val totalSeconds = elapsedTimeMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return "%02d:%02d".format(minutes, seconds)
}