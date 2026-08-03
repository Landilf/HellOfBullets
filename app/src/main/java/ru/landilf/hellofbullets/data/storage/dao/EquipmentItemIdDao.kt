package ru.landilf.hellofbullets.data.storage.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import ru.landilf.hellofbullets.data.storage.entities.equipment.EquipmentItemIdCounterEntity

@Dao
interface EquipmentItemIdDao {
    @Query(
        """
            SELECT * FROM equipment_item_id_counter
            WHERE counterId = :counterId
        """
    )
    suspend fun getCounter(
        counterId: Int = EquipmentItemIdCounterEntity.COUNTER_ID
    ): EquipmentItemIdCounterEntity?

    @Upsert
    suspend fun upsertCounter(counter: EquipmentItemIdCounterEntity)

    @Transaction
    suspend fun generateId(): Long {
        val counter = getCounter()

        if (counter == null) {
            upsertCounter(
                EquipmentItemIdCounterEntity(
                    nextItemId = FIRST_ITEM_ID + 1
                )
            )
            return FIRST_ITEM_ID
        }

        check(counter.nextItemId < Long.MAX_VALUE) {
            "Достигнут максимальный идентификатор предмета"
        }

        upsertCounter(
            counter.copy(
                nextItemId = counter.nextItemId + 1
            )
        )

        return counter.nextItemId
    }

    private companion object {
        const val FIRST_ITEM_ID = 1L
    }
}