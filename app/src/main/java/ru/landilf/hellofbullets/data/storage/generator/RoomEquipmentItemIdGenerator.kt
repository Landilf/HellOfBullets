package ru.landilf.hellofbullets.data.storage.generator

import ru.landilf.hellofbullets.data.storage.dao.EquipmentItemIdDao
import ru.landilf.hellofbullets.domain.generator.EquipmentItemIdGenerator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomEquipmentItemIdGenerator @Inject constructor(
    private val equipmentItemIdDao: EquipmentItemIdDao
) : EquipmentItemIdGenerator {
    override suspend fun generateId(): Long {
        return equipmentItemIdDao.generateId()
    }
}