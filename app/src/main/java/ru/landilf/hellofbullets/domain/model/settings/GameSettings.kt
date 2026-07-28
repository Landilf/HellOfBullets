package ru.landilf.hellofbullets.domain.model.settings

data class GameSettings(
    val inputSensitivity: Float = DEFAULT_INPUT_SENSITIVITY
) {
    init {
        require(inputSensitivity in MIN_INPUT_SENSITIVITY..MAX_INPUT_SENSITIVITY)
    }

    companion object {
        const val DEFAULT_INPUT_SENSITIVITY = 1f
        const val MIN_INPUT_SENSITIVITY = 0.5f
        const val MAX_INPUT_SENSITIVITY = 2f
    }
}
