package ru.landilf.hellofbullets.presentation.survival.game.component

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.BulletProjectile
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.LaserProjectile
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.Projectile
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.RocketProjectile
import ru.landilf.hellofbullets.domain.model.battle.survival.SurvivalGameState
import ru.landilf.hellofbullets.domain.model.common.GameFieldSize
import ru.landilf.hellofbullets.domain.model.common.Vector2

@Composable
fun SurvivalGameCanvas(
    gameState: SurvivalGameState,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
    ) {
        drawGameField(gameState)
    }
}

private fun DrawScope.drawGameField(
    gameState: SurvivalGameState
) {
    drawRect(
        color = Color(0xFF16212B)
    )

    drawPlayer(
        position = gameState.playerRuntimeState.position,
        radius = gameState.playerRuntimeState.hitRadius,
        isAlive = gameState.playerRuntimeState.isAlive,
        fieldSize = gameState.fieldSize
    )

    gameState.activeProjectiles.forEach { projectile ->
        drawProjectile(projectile, gameState.fieldSize)
    }
}

private fun DrawScope.drawPlayer(
    position: Vector2,
    radius: Float,
    isAlive: Boolean,
    fieldSize: GameFieldSize
) {
    drawCircle(
        color = if (isAlive) Color(0xFF7CFFB2) else Color.Red,
        radius = radius.toCanvasPx(fieldSize, size.width),
        center = position.toCanvasOffset(fieldSize, size.width, size.height)
    )
}

private fun DrawScope.drawProjectile(
    projectile: Projectile,
    fieldSize: GameFieldSize
) {
    when (projectile) {
        is BulletProjectile -> {
            drawCircle(
                color = Color(0xFFFFD166),
                radius = projectile.hitRadius.toCanvasPx(fieldSize, size.width),
                center = projectile.position.toCanvasOffset(fieldSize, size.width, size.height)
            )
        }

        is LaserProjectile -> {
            drawLine(
                color = Color(0xFF70D6FF),
                start = projectile.startPosition.toCanvasOffset(fieldSize, size.width, size.height),
                end = projectile.endPosition.toCanvasOffset(fieldSize, size.width, size.height),
                strokeWidth = projectile.hitRadius.toCanvasPx(fieldSize, size.width)
            )
        }

        is RocketProjectile -> {
            drawCircle(
                color = Color(0xFFFF6B6B),
                radius = projectile.hitRadius.toCanvasPx(fieldSize, size.width),
                center = projectile.position.toCanvasOffset(fieldSize, size.width, size.height),
                style = Stroke(width = 3.dp.toPx())
            )
        }
    }
}

private fun Vector2.toCanvasOffset(
    fieldSize: GameFieldSize,
    canvasWidth: Float,
    canvasHeight: Float
): Offset {
    return Offset(
        x = x / fieldSize.width * canvasWidth,
        y = y / fieldSize.height * canvasHeight
    )
}

private fun Float.toCanvasPx(
    fieldSize: GameFieldSize,
    canvasWidth: Float,
): Float {
    return this / fieldSize.width * canvasWidth
}