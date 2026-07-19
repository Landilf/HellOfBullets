package ru.landilf.hellofbullets.domain.model.config.survival

import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.AttackWave

data class DefaultSurvivalGameConfig(
    val playerConfig: DefaultSurvivalPlayerConfig,
    val waves: List<AttackWave>
)