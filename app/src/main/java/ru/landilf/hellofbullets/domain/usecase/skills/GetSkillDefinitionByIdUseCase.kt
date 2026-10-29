package ru.landilf.hellofbullets.domain.usecase.skills

import ru.landilf.hellofbullets.domain.model.skills.definition.SkillDefinition
import ru.landilf.hellofbullets.domain.repository.SkillDefinitionRepository
import javax.inject.Inject

class GetSkillDefinitionByIdUseCase @Inject constructor(
    private val skillDefinitionRepository: SkillDefinitionRepository
) {
    operator fun invoke(skillId: Long): SkillDefinition? {
        return skillDefinitionRepository.getDefinitionById(skillId)
    }
}