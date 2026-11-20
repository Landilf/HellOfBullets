package ru.landilf.hellofbullets.domain.engine.battle.common.skill

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.landilf.hellofbullets.domain.fixtures.PlayerTestFixtures.createPlayerStats
import ru.landilf.hellofbullets.domain.model.battle.common.skill.ActiveBattleSkillRuntimeState
import ru.landilf.hellofbullets.domain.model.battle.common.skill.BattleSkillLoadout
import ru.landilf.hellofbullets.domain.model.battle.common.skill.BattleSkillRuntimeState
import ru.landilf.hellofbullets.domain.model.battle.common.skill.DurationBonusBattleSkill
import ru.landilf.hellofbullets.domain.model.battle.common.skill.ShieldBattleSkill
import ru.landilf.hellofbullets.domain.model.skills.SkillSlot

class BattleSkillActivatorTest {
    private val activator = BattleSkillActivator(
        parametersCalculator = ActiveBattleSkillParametersCalculator()
    )

    private val shieldSkill = ShieldBattleSkill(
        skillId = 1L,
        durationMs = 2_000,
        cooldownMs = 20_000
    )

    @Test
    fun `activates available skill with calculated parameters`() {
        val runtimeState = createRuntimeState()
        val loadout = BattleSkillLoadout(
            firstSlotSkill = shieldSkill,
            secondSlotSkill = DurationBonusBattleSkill(
                skillId = 2L,
                bonusPercent = 40f
            )
        )

        val result = activator.activate(
            runtimeState = runtimeState,
            loadout = loadout,
            playerStats = createPlayerStats(effectDurationBonus = 10),
            slot = SkillSlot.FIRST
        )

        assertEquals(3_000, result.firstSlotActiveSkill?.remainingEffectMs)
        assertEquals(20_000, result.firstSlotActiveSkill?.remainingCooldownMs)
    }

    @Test
    fun `keeps state unchanged when skill is on cooldown`() {
        val runtimeState = createRuntimeState(
            remainingEffectMs = 1_000,
            remainingCooldownMs = 10_000
        )

        val result = activator.activate(
            runtimeState = runtimeState,
            loadout = BattleSkillLoadout(
                firstSlotSkill = shieldSkill,
                secondSlotSkill = null
            ),
            playerStats = createPlayerStats(),
            slot = SkillSlot.FIRST
        )

        assertEquals(runtimeState, result)
    }

    @Test
    fun `refreshes effect when cooldown ends before current effect`() {
        val runtimeState = createRuntimeState(
            remainingEffectMs = 500,
            remainingCooldownMs = 0
        )

        val result = activator.activate(
            runtimeState = runtimeState,
            loadout = BattleSkillLoadout(
                firstSlotSkill = shieldSkill,
                secondSlotSkill = null
            ),
            playerStats = createPlayerStats(),
            slot = SkillSlot.FIRST
        )

        assertEquals(2_000, result.firstSlotActiveSkill?.remainingEffectMs)
        assertEquals(20_000, result.firstSlotActiveSkill?.remainingCooldownMs)
    }

    private fun createRuntimeState(
        remainingEffectMs: Int = 0,
        remainingCooldownMs: Int = 0
    ): BattleSkillRuntimeState {
        return BattleSkillRuntimeState(
            firstSlotActiveSkill = ActiveBattleSkillRuntimeState(
                skill = shieldSkill,
                remainingEffectMs = remainingEffectMs,
                remainingCooldownMs = remainingCooldownMs
            ),
            secondSlotActiveSkill = null
        )
    }
}