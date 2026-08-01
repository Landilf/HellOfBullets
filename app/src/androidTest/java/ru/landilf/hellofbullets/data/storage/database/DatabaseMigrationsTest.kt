package ru.landilf.hellofbullets.data.storage.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class DatabaseMigrationsTest {
    private lateinit var context: Context
    private lateinit var databaseName: String

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
    fun migrateFromVersion3ToVersion4AndPreservesPlayerProfile() = runBlocking {
        createVersion3Database()

        val migratedDatabase = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            databaseName
        )
            .addMigrations(DatabaseMigrations.MIGRATION_3_4)
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