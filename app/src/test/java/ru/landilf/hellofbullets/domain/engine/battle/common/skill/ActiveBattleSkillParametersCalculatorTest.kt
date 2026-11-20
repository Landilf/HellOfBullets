package ru.landilf.hellofbullets.domain.engine.battle.common.skill

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.landilf.hellofbullets.domain.fixtures.PlayerTestFixtures.createPlayerStats
import ru.landilf.hellofbullets.domain.model.battle.common.skill.BattleSkillLoadout
import ru.landilf.hellofbullets.domain.model.battle.common.skill.CooldownReductionBattleSkill
import ru.landilf.hellofbullets.domain.model.battle.common.skill.DurationBonusBattleSkill
import ru.landilf.hellofbullets.domain.model.battle.common.skill.ShieldBattleSkill

class ActiveBattleSkillParametersCalculatorTest {
    private val calculator = ActiveBattleSkillParametersCalculator()

    private val shieldSkill = ShieldBattleSkill(
        skillId = 1L,
        durationMs = 2_000,
        cooldownMs = 20_000
    )

    @Test
    fun `returns base parameters without bonuses`() {
        val result = calculator.calculate(
            skill = shieldSkill,
            playerStats = createPlayerStats(),
            loadout = BattleSkillLoadout(
                firstSlotSkill = null,
                secondSlotSkill = null
            )
        )

        assertEquals(2_000, result.durationMs)
        assertEquals(20_000, result.cooldownMs)
    }

    @Test
    fun `adds duration bonuses from player stats and passive skill`() {
        val result = calculator.calculate(
            skill = shieldSkill,
            playerStats = createPlayerStats(effectDurationBonus = 10),
            loadout = BattleSkillLoadout(
                firstSlotSkill = DurationBonusBattleSkill(
                    skillId = 2L,
                    bonusPercent = 40f
                ),
                secondSlotSkill = null
            )
        )

        assertEquals(3_000, result.durationMs)
        assertEquals(20_000, result.cooldownMs)
    }

    @Test
    fun `adds cooldown reduction from player stats and passive skill`() {
        val result = calculator.calculate(
            skill = shieldSkill,
            playerStats = createPlayerStats(cooldownReduction = 15),
            loadout = BattleSkillLoadout(
                firstSlotSkill = CooldownReductionBattleSkill(
                    skillId = 2L,
                    bonusPercent = 20f
                ),
                secondSlotSkill = null
            )
        )

        assertEquals(2_000, result.durationMs)
        assertEquals(13_000, result.cooldownMs)
    }

    @Test
    fun `keeps cooldown at minimum value when total reduction exceeds one hundred percent`() {
        val result = calculator.calculate(
            skill = shieldSkill,
            playerStats = createPlayerStats(cooldownReduction = 80),
            loadout = BattleSkillLoadout(
                firstSlotSkill = CooldownReductionBattleSkill(
                    skillId = 2L,
                    bonusPercent = 40f
                ),
                secondSlotSkill = null
            )
        )

        assertEquals(500, result.cooldownMs)
    }
}