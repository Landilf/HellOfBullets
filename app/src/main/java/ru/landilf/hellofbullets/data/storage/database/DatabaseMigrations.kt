package ru.landilf.hellofbullets.data.storage.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                    ALTER TABLE player_profile
                    ADD COLUMN skillPointAmount INTEGER NOT NULL DEFAULT 0
                """.trimIndent()
            )
        }
    }

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                    CREATE TABLE IF NOT EXISTS `player_build` (
                        `playerId` INTEGER NOT NULL,
                        `equippedWeaponItemId` INTEGER,
                        `equippedArmorItemId` INTEGER,
                        `equippedArtifactItemId` INTEGER,
                        PRIMARY KEY(`playerId`),
                        FOREIGN KEY(`playerId`) REFERENCES `player_profile`(`id`)
                        ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """.trimIndent()
            )

            db.execSQL(
                """
                    CREATE TABLE IF NOT EXISTS `weapon_items` (
                        `id` INTEGER NOT NULL,
                        `ownerId` INTEGER NOT NULL,
                        `definitionId` INTEGER NOT NULL,
                        `level` INTEGER NOT NULL,
                        `qualityName` TEXT NOT NULL,
                        `additionalStatTypeName` TEXT NOT NULL,
                        `additionalStatValue` REAL NOT NULL,
                        `damage` REAL NOT NULL,
                        `attackSpeed` REAL NOT NULL,
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`ownerId`) REFERENCES `player_profile`(`id`)
                        ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """.trimIndent()
            )

            db.execSQL(
                """
                    CREATE TABLE IF NOT EXISTS `armor_items` (
                        `id` INTEGER NOT NULL,
                        `ownerId` INTEGER NOT NULL,
                        `definitionId` INTEGER NOT NULL,
                        `level` INTEGER NOT NULL,
                        `qualityName` TEXT NOT NULL,
                        `additionalStatTypeName` TEXT NOT NULL,
                        `additionalStatValue` REAL NOT NULL,
                        `hp` REAL NOT NULL,
                        `defense` REAL NOT NULL,
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`ownerId`) REFERENCES `player_profile`(`id`)
                        ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """.trimIndent()
            )

            db.execSQL(
                """
                    CREATE TABLE IF NOT EXISTS `artifact_items` (
                        `id` INTEGER NOT NULL,
                        `ownerId` INTEGER NOT NULL,
                        `definitionId` INTEGER NOT NULL,
                        `level` INTEGER NOT NULL,
                        `qualityName` TEXT NOT NULL,
                        `additionalStatTypeName` TEXT NOT NULL,
                        `additionalStatValue` REAL NOT NULL,
                        `cooldownReductionPercent` REAL NOT NULL,
                        `durationBonusPercent` REAL NOT NULL,
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`ownerId`) REFERENCES `player_profile`(`id`)
                        ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """.trimIndent()
            )

            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_weapon_items_ownerId` " +
                        "ON `weapon_items` (`ownerId`)"
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_armor_items_ownerId` " +
                        "ON `armor_items` (`ownerId`)"
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_artifact_items_ownerId` " +
                        "ON `artifact_items` (`ownerId`)"
            )
        }
    }
}