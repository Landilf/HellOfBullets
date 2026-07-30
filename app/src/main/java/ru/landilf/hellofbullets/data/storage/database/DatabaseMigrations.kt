package ru.landilf.hellofbullets.data.storage.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(
            db: SupportSQLiteDatabase
        ) {
            db.execSQL(
                """
                    ALTER TABLE player_profile
                    ADD COLUMN skillPointAmount INTEGER NOT NULL DEFAULT 0
                """.trimIndent()
            )
        }
    }
}