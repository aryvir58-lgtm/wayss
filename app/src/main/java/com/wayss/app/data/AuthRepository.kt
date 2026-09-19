package com.wayss.app.data

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    val currentUser get() = auth.currentUser

    suspend fun signInEmail(email: String, password: String) =
        auth.signInWithEmailAndPassword(email, password).await()

    suspend fun createEmail(email: String, password: String) =
        auth.createUserWithEmailAndPassword(email, password).await()

    suspend fun sendPasswordReset(email: String) =
        auth.sendPasswordResetEmail(email).await()

    fun signOut() = auth.signOut()
}
