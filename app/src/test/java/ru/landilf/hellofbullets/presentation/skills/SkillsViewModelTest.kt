package ru.landilf.hellofbullets.presentation.skills

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import ru.landilf.hellofbullets.domain.fixtures.PlayerTestFixtures.createPlayerState
import ru.landilf.hellofbullets.domain.model.skills.PassiveSkillStat
import ru.landilf.hellofbullets.domain.model.skills.SkillProgress
import ru.landilf.hellofbullets.domain.model.skills.SkillSlot
import ru.landilf.hellofbullets.domain.model.skills.definition.ActiveSkillLevelParameters
import ru.landilf.hellofbullets.domain.model.skills.definition.PassiveSkillDefinition
import ru.landilf.hellofbullets.domain.model.skills.definition.ShieldSkillDefinition
import ru.landilf.hellofbullets.domain.model.skills.definition.SkillDefinition
import ru.landilf.hellofbullets.domain.usecase.FakePlayerRepository
import ru.landilf.hellofbullets.domain.usecase.FakeSkillDefinitionRepository
import ru.landilf.hellofbullets.domain.usecase.player.GetOrCreatePlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.player.LoadPlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.player.ObservePlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.player.SavePlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.skills.GetSkillDefinitionByIdUseCase
import ru.landilf.hellofbullets.domain.usecase.skills.GetSkillDefinitionsUseCase
import ru.landilf.hellofbullets.domain.usecase.skills.SetEquippedSkillUseCase
import ru.landilf.hellofbullets.domain.usecase.skills.UnlockPlayerSkillUseCase
import ru.landilf.hellofbullets.domain.usecase.skills.UpgradePlayerSkillUseCase
import ru.landilf.hellofbullets.fixtures.MainDispatcherRule

class SkillsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val activeDefinition = ShieldSkillDefinition(
        id = 1L,
        unlockCost = 5,
        levels = List(2) {
            ActiveSkillLevelParameters(
                durationMs = 2_000,
                cooldownMs = 20_000
            )
        }
    )

    private val passiveDefinition = PassiveSkillDefinition(
        id = 2L,
        unlockCost = 5,
        stat = PassiveSkillStat.DURATION_BONUS,
        bonusPercentByLevel = listOf(20f, 40f)
    )

    @Test
    fun `maps player progress into active and passive skills`() {
        val playerRepository = FakePlayerRepository(
            initialState = createPlayerState(
                skillPointAmount = 8,
                firstSkillId = activeDefinition.id,
                skillProgresses = listOf(
                    SkillProgress(activeDefinition.id, level = 2),
                    SkillProgress(passiveDefinition.id, level = 1)
                )
            )
        )
        val viewModel = createViewModel(playerRepository)
        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertEquals(8, state.skillPointAmount)
        assertEquals(1, state.activeSkills.size)
        assertEquals(1, state.passiveSkills.size)
        assertEquals(SkillSlot.FIRST, state.activeSkills.single().equippedSlot)
        assertEquals(1, state.passiveSkills.single().level)
        assertEquals(null, state.passiveSkills.single().equippedSlot)
    }

    @Test
    fun `unlocks skill through action`() {
        val playerRepository = FakePlayerRepository(
            initialState = createPlayerState(
                skillPointAmount = 7
            )
        )
        val viewModel = createViewModel(playerRepository)

        viewModel.onAction(
            SkillAction.OnUnlockClick(passiveDefinition.id)
        )

        assertEquals(2, playerRepository.state?.playerProfile?.skillPointAmount)
        assertEquals(1, viewModel.uiState.value.passiveSkills.single().level)
    }

    @Test
    fun `equips active skill through action`() {
        val playerRepository = FakePlayerRepository(
            initialState = createPlayerState(
                skillProgresses = listOf(
                    SkillProgress(activeDefinition.id, level = 1)
                )
            )
        )
        val viewModel = createViewModel(playerRepository)

        viewModel.onAction(
            SkillAction.OnEquipClick(
                skillId = activeDefinition.id,
                slot = SkillSlot.SECOND
            )
        )

        assertEquals(
            activeDefinition.id,
            playerRepository.state?.playerBuild?.secondSkillId
        )
        assertEquals(
            SkillSlot.SECOND,
            viewModel.uiState.value.activeSkills.single().equippedSlot
        )
    }

    @Test
    fun `equips passive skill through action`() {
        val playerRepository = FakePlayerRepository(
            initialState = createPlayerState(
                skillProgresses = listOf(
                    SkillProgress(
                        skillId = passiveDefinition.id,
                        level = 1
                    )
                )
            )
        )
        val viewModel = createViewModel(playerRepository)

        viewModel.onAction(
            SkillAction.OnEquipClick(
                skillId = passiveDefinition.id,
                slot = SkillSlot.FIRST
            )
        )

        assertEquals(
            passiveDefinition.id,
            playerRepository.state?.playerBuild?.firstSkillId
        )
        assertEquals(
            SkillSlot.FIRST,
            viewModel.uiState.value.passiveSkills.single().equippedSlot
        )
    }

    @Test
    fun `upgrades skill through action`() {
        val playerRepository = FakePlayerRepository(
            initialState = createPlayerState(
                skillPointAmount = 5,
                skillProgresses = listOf(
                    SkillProgress(
                        skillId = activeDefinition.id,
                        level = 1
                    )
                )
            )
        )
        val viewModel = createViewModel(playerRepository)

        viewModel.onAction(
            SkillAction.OnUpgradeClick(activeDefinition.id)
        )

        assertEquals(
            3,
            playerRepository.state?.playerProfile?.skillPointAmount
        )
        assertEquals(
            2,
            viewModel.uiState.value.activeSkills.single().level
        )
    }

    private fun createViewModel(
        playerRepository: FakePlayerRepository,
        definitions: List<SkillDefinition> = listOf(
            activeDefinition,
            passiveDefinition
        )
    ): SkillsViewModel {
        val skillDefinitionRepository = FakeSkillDefinitionRepository(definitions)
        val getSkillDefinitionByIdUseCase = GetSkillDefinitionByIdUseCase(
            skillDefinitionRepository
        )
        val savePlayerStateUseCase = SavePlayerStateUseCase(playerRepository)

        return SkillsViewModel(
            getOrCreatePlayerStateUseCase = GetOrCreatePlayerStateUseCase(
                loadPlayerStateUseCase = LoadPlayerStateUseCase(playerRepository),
                savePlayerStateUseCase = savePlayerStateUseCase
            ),
            observePlayerStateUseCase = ObservePlayerStateUseCase(playerRepository),
            getSkillDefinitionsUseCase = GetSkillDefinitionsUseCase(skillDefinitionRepository),
            unlockPlayerSkillUseCase = UnlockPlayerSkillUseCase(
                playerRepository = playerRepository,
                getSkillDefinitionByIdUseCase = getSkillDefinitionByIdUseCase
            ),
            upgradePlayerSkillUseCase = UpgradePlayerSkillUseCase(
                playerRepository = playerRepository,
                getSkillDefinitionByIdUseCase = getSkillDefinitionByIdUseCase
            ),
            setEquippedSkillUseCase = SetEquippedSkillUseCase(
                playerRepository = playerRepository
            )
        )
    }
}