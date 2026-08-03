package ru.landilf.hellofbullets.data.storage.entities.equipment

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equipment_item_id_counter")
data class EquipmentItemIdCounterEntity(
    @PrimaryKey
    val counterId: Int = COUNTER_ID,
    val nextItemId: Long
) {
    companion object {
        const val COUNTER_ID = 0
    }
}
