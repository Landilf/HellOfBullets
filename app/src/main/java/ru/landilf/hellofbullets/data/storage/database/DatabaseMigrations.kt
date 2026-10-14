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

    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                    CREATE TABLE IF NOT EXISTS `equipment_item_id_counter` (
                        `counterId` INTEGER NOT NULL,
                        `nextItemId` INTEGER NOT NULL,
                        PRIMARY KEY(`counterId`)
                    )
                """.trimIndent()
            )
            db.execSQL(
                """
                    INSERT INTO `equipment_item_id_counter` (
                        `counterId`,
                        `nextItemId`
                    )
                    SELECT
                        0,
                        COALESCE(MAX(`id`), 0) + 1
                    FROM (
                        SELECT `id` FROM `weapon_items`
                        UNION ALL
                        SELECT `id` FROM `armor_items`
                        UNION ALL
                        SELECT `id` FROM `artifact_items`
                    )
                """.trimIndent()
            )
        }
    }

    val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                    ALTER TABLE `weapon_items`
                    ADD COLUMN `specializationCoef` REAL NOT NULL DEFAULT 0.0
                """.trimIndent()
            )
            db.execSQL(
                """
                    ALTER TABLE `armor_items`
                    ADD COLUMN `specializationCoef` REAL NOT NULL DEFAULT 0.0
                """.trimIndent()
            )
            db.execSQL(
                """
                    ALTER TABLE `artifact_items`
                    ADD COLUMN `specializationCoef` REAL NOT NULL DEFAULT 0.0
                """.trimIndent()
            )
        }
    }

    val MIGRATION_6_7 = object : Migration(6, 7) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                    CREATE TABLE IF NOT EXISTS `shop_state` (
                        `id` INTEGER NOT NULL,
                        `lastAutomaticRefreshEpochDay` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent()
            )
            db.execSQL(
                """
                    CREATE TABLE IF NOT EXISTS `shop_weapon_offers` (
                        `itemId` INTEGER NOT NULL,
                        `position` INTEGER NOT NULL,
                        `definitionId` INTEGER NOT NULL,
                        `level` INTEGER NOT NULL,
                        `qualityName` TEXT NOT NULL,
                        `additionalStatTypeName` TEXT NOT NULL,
                        `additionalStatValue` REAL NOT NULL,
                        `damage` REAL NOT NULL,
                        `attackSpeed` REAL NOT NULL,
                        `specializationCoef` REAL NOT NULL,
                        `purchasePrice` INTEGER NOT NULL,
                        PRIMARY KEY(`itemId`)
                    )
                """.trimIndent()
            )
            db.execSQL(
                """
                    CREATE TABLE IF NOT EXISTS `shop_armor_offers` (
                        `itemId` INTEGER NOT NULL,
                        `position` INTEGER NOT NULL,
                        `definitionId` INTEGER NOT NULL,
                        `level` INTEGER NOT NULL,
                        `qualityName` TEXT NOT NULL,
                        `additionalStatTypeName` TEXT NOT NULL,
                        `additionalStatValue` REAL NOT NULL,
                        `hp` REAL NOT NULL,
                        `defense` REAL NOT NULL,
                        `specializationCoef` REAL NOT NULL,
                        `purchasePrice` INTEGER NOT NULL,
                        PRIMARY KEY(`itemId`)
                    )
                """.trimIndent()
            )
            db.execSQL(
                """
                    CREATE TABLE IF NOT EXISTS `shop_artifact_offers` (
                        `itemId` INTEGER NOT NULL,
                        `position` INTEGER NOT NULL,
                        `definitionId` INTEGER NOT NULL,
                        `level` INTEGER NOT NULL,
                        `qualityName` TEXT NOT NULL,
                        `additionalStatTypeName` TEXT NOT NULL,
                        `additionalStatValue` REAL NOT NULL,
                        `cooldownReductionPercent` REAL NOT NULL,
                        `durationBonusPercent` REAL NOT NULL,
                        `specializationCoef` REAL NOT NULL,
                        `purchasePrice` INTEGER NOT NULL,
                        PRIMARY KEY(`itemId`)
                    )
                """.trimIndent()
            )
        }
    }

    val MIGRATION_7_8 = object : Migration(7, 8) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                    ALTER TABLE `shop_state`
                    ADD COLUMN `manualRefreshCount` INTEGER NOT NULL DEFAULT 0
                """.trimIndent()
            )
        }
    }

    val MIGRATION_8_9 = object : Migration(8, 9) {
        override fun migrate(db: SupportSQLiteDatabase) {
            listOf(
                "shop_weapon_offers",
                "shop_armor_offers",
                "shop_artifact_offers"
            ).forEach { tableName ->
                db.execSQL(
                    """
                        ALTER TABLE `$tableName`
                        ADD COLUMN `isSold` INTEGER NOT NULL DEFAULT 0
                    """.trimIndent()
                )
            }
        }
    }
}