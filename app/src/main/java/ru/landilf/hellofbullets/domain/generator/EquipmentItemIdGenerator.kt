package ru.landilf.hellofbullets.domain.generator

interface EquipmentItemIdGenerator {
    suspend fun generateId(): Long
}