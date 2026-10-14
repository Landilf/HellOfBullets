package ru.landilf.hellofbullets.domain.usecase.equipment

import ru.landilf.hellofbullets.domain.model.equipment.definition.EquipmentDefinition
import ru.landilf.hellofbullets.domain.repository.EquipmentDefinitionRepository
import javax.inject.Inject

class GetEquipmentDefinitionByIdUseCase @Inject constructor(
    private val equipmentDefinitionRepository: EquipmentDefinitionRepository
) {
    operator fun invoke(definitionId: Long): EquipmentDefinition? {
        return equipmentDefinitionRepository.getDefinitionById(definitionId)
    }
}