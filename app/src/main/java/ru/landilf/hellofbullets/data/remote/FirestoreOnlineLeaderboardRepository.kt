package ru.landilf.hellofbullets.data.remote

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import ru.landilf.hellofbullets.data.remote.auth.FirebaseAuthDataSource
import ru.landilf.hellofbullets.domain.model.leaderboard.LeaderboardRecord
import ru.landilf.hellofbullets.domain.repository.OnlineLeaderboardRepository
import javax.inject.Inject

class FirestoreOnlineLeaderboardRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuthDataSource: FirebaseAuthDataSource
) : OnlineLeaderboardRepository {
    private val survivalRecordsCollection = firestore
        .collection(COLLECTION_LEADERBOARD)
        .document(DOCUMENT_SURVIVAL)
        .collection(COLLECTION_RECORDS)

    override suspend fun getOrCreatePlayerId(): String {
        return firebaseAuthDataSource.getOrCreateUserId()
    }

    override suspend fun getTopSurvivalRecords(
        limit: Int
    ): List<LeaderboardRecord> {
        require(limit > 0) {
            "Размер онлайн-таблицы рекордов должен быть положительным"
        }

        return survivalRecordsCollection
            .orderBy(FIELD_TIME, Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .get()
            .await()
            .documents
            .mapNotNull { document ->
                val playerName = document.getString(FIELD_PLAYER_NAME)
                    ?: return@mapNotNull null
                val time = document.getLong(FIELD_TIME)
                    ?.toInt()
                    ?: return@mapNotNull null

                LeaderboardRecord(
                    id = document.id,
                    playerName = playerName,
                    time = time
                )
            }
    }

    override suspend fun submitSurvivalRecord(
        playerId: String,
        playerName: String,
        time: Int
    ) {
        require(time >= 0) {
            "Время рекорда не может быть отрицательным"
        }

        val recordDocument = survivalRecordsCollection
            .document(playerId)

        firestore.runTransaction { transaction ->
            val currentRecord = transaction.get(recordDocument)
            val currentTime = currentRecord
                .getLong(FIELD_TIME)
                ?.toInt()
            val currentPlayerName = currentRecord.getString(FIELD_PLAYER_NAME)

            if (currentTime == null ||
                time > currentTime ||
                playerName != currentPlayerName
            ) {
                transaction.set(
                    recordDocument,
                    mapOf(
                        FIELD_USER_ID to playerId,
                        FIELD_PLAYER_NAME to playerName,
                        FIELD_TIME to time,
                        FIELD_UPDATED_AT to FieldValue.serverTimestamp()
                    )
                )
            }
        }.await()
    }

    private companion object {
        const val COLLECTION_LEADERBOARD = "leaderboards"
        const val DOCUMENT_SURVIVAL = "survival"
        const val COLLECTION_RECORDS = "records"

        const val FIELD_USER_ID = "userId"
        const val FIELD_PLAYER_NAME = "playerName"
        const val FIELD_TIME = "time"
        const val FIELD_UPDATED_AT = "updatedAt"
    }
}