package ru.landilf.hellofbullets.domain.model.battle.survival

import ru.landilf.hellofbullets.domain.model.battle.common.projectile.Projectile
import ru.landilf.hellofbullets.domain.model.player.PlayerRuntimeState
import ru.landilf.hellofbullets.domain.model.player.PlayerStats

data class SurvivalGameState(
    val phase: SurvivalPhase,
    val elapsedTimeMs: Int,
    val playerStats: PlayerStats,
    val playerRuntimeState: PlayerRuntimeState,
    val activeProjectiles: List<Projectile>
)
