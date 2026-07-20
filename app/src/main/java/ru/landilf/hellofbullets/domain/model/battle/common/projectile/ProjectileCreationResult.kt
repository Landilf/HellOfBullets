package ru.landilf.hellofbullets.domain.model.battle.common.projectile

import ru.landilf.hellofbullets.domain.model.battle.common.random.BattleRandomState

data class ProjectileCreationResult(
    val projectiles: List<Projectile>,
    val nextGenerationState: ProjectileGenerationState
)
