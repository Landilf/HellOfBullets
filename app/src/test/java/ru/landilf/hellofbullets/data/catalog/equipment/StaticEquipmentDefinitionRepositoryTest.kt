package ru.landilf.hellofbullets.data.catalog.equipment

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.landilf.hellofbullets.domain.model.equipment.definition.WeaponDefinition

class StaticEquipmentDefinitionRepositoryTest {
    private val repository = StaticEquipmentDefinitionRepository()

    @Test
    fun `returns definition with requested id`() {
        val definition = repository.getDefinitionById(1L)

        assertTrue(definition is WeaponDefinition)
        assertEquals(1L, definition?.id)
        assertEquals("Pistol", definition?.name)
    }

    @Test
    fun `returns null for unknown id`() {
        assertNull(repository.getDefinitionById(999L))
    }

    @Test
    fun `returns all configured definitions`() {
        val definitions = repository.getDefinitions()

        assertEquals(3, definitions.size)
        assertEquals(
            setOf(1L, 2L, 3L),
            definitions.map { it.id }.toSet()
        )
    }
}