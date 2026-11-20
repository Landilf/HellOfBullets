package ru.landilf.hellofbullets.domain.engine.battle.common.skill

import org.junit.Assert
import org.junit.Test
import ru.landilf.hellofbullets.domain.model.battle.common.skill.ActiveBattleSkillRuntimeState
import ru.landilf.hellofbullets.domain.model.battle.common.skill.BattleSkillRuntimeState
import ru.landilf.hellofbullets.domain.model.battle.common.skill.ShieldBattleSkill
import ru.landilf.hellofbullets.domain.model.battle.common.skill.TimeSlowBattleSkill

class BattleSkillRuntimeUpdaterTest {
    private val updater = BattleSkillRuntimeUpdater()

    @Test
    fun `updates effect and cooldown timers for both slots`() {
        val runtimeState = BattleSkillRuntimeState(
            firstSlotActiveSkill = ActiveBattleSkillRuntimeState(
                skill = TimeSlowBattleSkill(
                    skillId = 1L,
                    durationMs = 3_000,
                    cooldownMs = 30_000,
                    slowdownPercent = 50f
                ),
                remainingEffectMs = 2_000,
                remainingCooldownMs = 10_000
            ),
            secondSlotActiveSkill = ActiveBattleSkillRuntimeState(
                skill = ShieldBattleSkill(
                    skillId = 2L,
                    durationMs = 2_000,
                    cooldownMs = 20_000
                ),
                remainingEffectMs = 500,
                remainingCooldownMs = 1_000
            )
        )

        val result = updater.update(
            runtimeState = runtimeState,
            deltaTimeMs = 750
        )

        Assert.assertEquals(
            1_250,
            result.firstSlotActiveSkill?.remainingEffectMs
        )
        Assert.assertEquals(
            9_250,
            result.firstSlotActiveSkill?.remainingCooldownMs
        )
        Assert.assertEquals(
            0,
            result.secondSlotActiveSkill?.remainingEffectMs
        )
        Assert.assertEquals(
            250,
            result.secondSlotActiveSkill?.remainingCooldownMs
        )
    }

    @Test
    fun `keeps empty slots empty`() {
        val runtimeState = BattleSkillRuntimeState(
            firstSlotActiveSkill = null,
            secondSlotActiveSkill = null
        )

        val result = updater.update(
            runtimeState = runtimeState,
            deltaTimeMs = 100
        )

        Assert.assertEquals(runtimeState, result)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throws for negative delta time`() {
        updater.update(
            runtimeState = BattleSkillRuntimeState(
                firstSlotActiveSkill = null,
                secondSlotActiveSkill = null
            ),
            deltaTimeMs = -1
        )
    }
}