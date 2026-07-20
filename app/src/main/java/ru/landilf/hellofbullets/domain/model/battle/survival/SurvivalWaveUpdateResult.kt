package ru.landilf.hellofbullets.domain.model.battle.survival

import ru.landilf.hellofbullets.domain.model.battle.common.projectile.Projectile
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.ProjectileGenerationState
import ru.landilf.hellofbullets.domain.model.battle.common.random.BattleRandomState

data class SurvivalWaveUpdateResult(
    val waveState: SurvivalWaveState?,
    val spawnedProjectiles: List<Projectile>,
    val nextGenerationState: ProjectileGenerationState
)
