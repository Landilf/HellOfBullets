package ru.landilf.hellofbullets.domain.model.config.survival

data class DefaultSurvivalGameConfig(
    val playerConfig: DefaultSurvivalPlayerConfig,
    val waveConfig: DefaultSurvivalWaveConfig
)