package ru.landilf.hellofbullets.data.remote.auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    suspend fun getOrCreateUserId(): String {
        firebaseAuth.currentUser?.uid?.let { userId ->
            return userId
        }

        return checkNotNull(
            firebaseAuth.signInAnonymously()
                .await()
                .user
                ?.uid
        ) {
            "Firebase не вернул идентификатор анонимного пользователя"
        }
    }
}