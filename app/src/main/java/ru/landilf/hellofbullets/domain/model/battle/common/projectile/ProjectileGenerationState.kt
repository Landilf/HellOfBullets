package ru.landilf.hellofbullets.domain.model.battle.common.projectile

import ru.landilf.hellofbullets.domain.model.battle.common.random.BattleRandomState

data class ProjectileGenerationState(
    val nextProjectileId: Long,
    val randomState: BattleRandomState
)
