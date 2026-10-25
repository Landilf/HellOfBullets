package ru.landilf.hellofbullets.domain.repository

import ru.landilf.hellofbullets.domain.model.skills.definition.SkillDefinition

interface SkillDefinitionRepository {
    fun getDefinitions(): List<SkillDefinition>

    fun getDefinitionById(id: Long): SkillDefinition?
}