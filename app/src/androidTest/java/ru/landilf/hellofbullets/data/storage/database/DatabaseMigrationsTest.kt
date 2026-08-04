package ru.landilf.hellofbullets.data.storage.database

import android.content.Context
import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.landilf.hellofbullets.data.storage.generator.RoomEquipmentItemIdGenerator
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class DatabaseMigrationsTest {
    private lateinit var context: Context
    private lateinit var databaseName: String
    private val migrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java
    )

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        databaseName = "migration_test_${UUID.randomUUID()}.db"
    }

    @After
    fun tearDown() {
        context.deleteDatabase(databaseName)
    }

    @Test
    fun migratesFromVersion3ToVersion5AndPreservesPlayerProfile() = runBlocking {
        createVersion3Database()

        val migratedDatabase = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            databaseName
        )
            .addMigrations(
                DatabaseMigrations.MIGRATION_3_4,
                DatabaseMigrations.MIGRATION_4_5,
                DatabaseMigrations.MIGRATION_5_6
            )
            .allowMainThreadQueries()
            .build()

        try {
            assertEquals(
                "Player",
                migratedDatabase.playerDao().getPlayerProfile()?.name
            )
            assertNull(migratedDatabase.playerDao().getPlayerBuild(1L))
            assertTrue(
                migratedDatabase.equipmentDao().getWeaponItems(1L).isEmpty()
            )
        } finally {
            migratedDatabase.close()
        }
    }

    @Test
    fun migratesFromVersion4ToVersion5AndInitializesNextEquipmentItemId() {
        val database = migrationTestHelper.createDatabase(databaseName, 4)

        database.execSQL(
            """
                INSERT INTO `player_profile` (
                    `id`, `name`, `level`, `expAmount`, `silverAmount`, `skillPointAmount`
                ) VALUES (1, 'Player', 1, 0, 0, 0)
            """.trimIndent()
        )
        database.execSQL(
            """
                INSERT INTO `weapon_items` (
                    `id`, `ownerId`, `definitionId`, `level`, `qualityName`,
                    `additionalStatTypeName`, `additionalStatValue`,
                    `damage`, `attackSpeed`
                ) VALUES (3, 1, 1, 1, 'NORMAL', 'DAMAGE', 0.0, 10.0, 1.0)
            """.trimIndent()
        )
        database.execSQL(
            """
                INSERT INTO `artifact_items` (
                    `id`, `ownerId`, `definitionId`, `level`, `qualityName`,
                    `additionalStatTypeName`, `additionalStatValue`,
                    `cooldownReductionPercent`, `durationBonusPercent`
                ) VALUES (7, 1, 3, 1, 'NORMAL', 'DURATION', 0.0, 5.0, 10.0)
            """.trimIndent()
        )
        database.close()

        val migratedDatabase = migrationTestHelper.runMigrationsAndValidate(
            databaseName,
            5,
            true,
            DatabaseMigrations.MIGRATION_4_5
        )

        migratedDatabase.query(
            "SELECT `nextItemId` FROM `equipment_item_id_counter` WHERE `counterId` = 0"
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(8L, cursor.getLong(0))
        }

        migratedDatabase.close()
    }

    @Test
    fun migratesFromVersion5ToVersion6AndAddsNeutralSpecializationCoef() {
        val database = migrationTestHelper.createDatabase(databaseName, 5)

        database.execSQL(
            """
                INSERT INTO `player_profile` (
                    `id`, `name`, `level`, `expAmount`, `silverAmount`, `skillPointAmount`
                ) VALUES (1, 'Player', 1, 0, 0, 0)
            """.trimIndent()
        )
        database.execSQL(
            """
                INSERT INTO `weapon_items` (
                    `id`, `ownerId`, `definitionId`, `level`, `qualityName`,
                    `additionalStatTypeName`, `additionalStatValue`,
                    `damage`, `attackSpeed`
                ) VALUES (3, 1, 1, 1, 'NORMAL', 'DAMAGE', 0.0, 10.0, 1.0)
            """.trimIndent()
        )
        database.execSQL(
            """
                INSERT INTO `armor_items` (
                    `id`, `ownerId`, `definitionId`, `level`, `qualityName`,
                    `additionalStatTypeName`, `additionalStatValue`,
                    `hp`, `defense`
                ) VALUES (5, 1, 1, 1, 'NORMAL', 'DAMAGE', 0.0, 100.0, 5.0)
            """.trimIndent()
        )
        database.execSQL(
            """
                INSERT INTO `artifact_items` (
                    `id`, `ownerId`, `definitionId`, `level`, `qualityName`,
                    `additionalStatTypeName`, `additionalStatValue`,
                    `cooldownReductionPercent`, `durationBonusPercent`
                ) VALUES (7, 1, 3, 1, 'NORMAL', 'DURATION', 0.0, 5.0, 10.0)
            """.trimIndent()
        )
        database.close()

        val migratedDatabase = migrationTestHelper.runMigrationsAndValidate(
            databaseName,
            6,
            true,
            DatabaseMigrations.MIGRATION_5_6
        )

        listOf("weapon_items", "armor_items", "artifact_items").forEach { tableName ->
            migratedDatabase.query(
                "SELECT `specializationCoef` FROM `$tableName`"
            ).use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals(0f, cursor.getFloat(0))
            }
        }

        migratedDatabase.close()
    }

    @Test
    fun generatesSequentialEquipmentItemIds(): Unit = runBlocking {
        val database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        try {
            val generator = RoomEquipmentItemIdGenerator(
                equipmentItemIdDao = database.equipmentItemIdDao()
            )

            assertEquals(1L, generator.generateId())
            assertEquals(2L, generator.generateId())
            assertEquals(3L, generator.generateId())
        } finally {
            database.close()
        }
    }

    private fun createVersion3Database() {
        val database = context.openOrCreateDatabase(
            databaseName,
            Context.MODE_PRIVATE,
            null
        )

        database.execSQL(
            """
                CREATE TABLE `player_profile` (
                    `id` INTEGER NOT NULL,
                    `name` TEXT NOT NULL,
                    `level` INTEGER NOT NULL,
                    `expAmount` INTEGER NOT NULL,
                    `silverAmount` INTEGER NOT NULL,
                    `skillPointAmount` INTEGER NOT NULL,
                    PRIMARY KEY(`id`)
                )
            """.trimIndent()
        )
        database.execSQL(
            """
                CREATE TABLE `leaderboard` (
                    `id` TEXT NOT NULL,
                    `playerName` TEXT NOT NULL,
                    `time` INTEGER NOT NULL,
                    PRIMARY KEY(`id`)
                )
            """.trimIndent()
        )
        database.execSQL(
            """
                INSERT INTO `player_profile` (
                    `id`,
                    `name`,
                    `level`,
                    `expAmount`,
                    `silverAmount`,
                    `skillPointAmount`
                ) VALUES (1, 'Player', 5, 250, 100, 4)
            """.trimIndent()
        )

        database.version = 3
        database.close()
    }

}