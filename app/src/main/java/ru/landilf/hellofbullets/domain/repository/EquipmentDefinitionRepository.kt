package ru.landilf.hellofbullets.domain.repository

import ru.landilf.hellofbullets.domain.model.equipment.definition.EquipmentDefinition

interface EquipmentDefinitionRepository {
    fun getDefinitions(): List<EquipmentDefinition>
    fun getDefinitionById(id: Long): EquipmentDefinition?
}