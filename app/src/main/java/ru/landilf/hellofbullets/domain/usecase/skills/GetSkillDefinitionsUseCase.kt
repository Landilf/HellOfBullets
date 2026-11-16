package ru.landilf.hellofbullets.domain.usecase.skills

import ru.landilf.hellofbullets.domain.model.skills.definition.SkillDefinition
import ru.landilf.hellofbullets.domain.repository.SkillDefinitionRepository
import javax.inject.Inject

class GetSkillDefinitionsUseCase @Inject constructor(
    private val skillDefinitionRepository: SkillDefinitionRepository
) {
    operator fun invoke(): List<SkillDefinition> {
        return skillDefinitionRepository.getDefinitions()
    }
}