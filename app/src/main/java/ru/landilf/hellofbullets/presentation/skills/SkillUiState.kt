package ru.landilf.hellofbullets.presentation.skills

data class SkillUiState(
    val isLoading: Boolean = true,
    val skillPointAmount: Int = 0,
    val activeSkills: List<SkillUiModel> = emptyList(),
    val passiveSkills: List<SkillUiModel> = emptyList(),
    val isSkillActionInProgress: Boolean = false,
    val errorMessage: String? = null
)