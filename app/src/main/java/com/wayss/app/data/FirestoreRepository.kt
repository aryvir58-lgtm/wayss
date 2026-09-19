package com.wayss.app.data

import com.google.firebase.firestore.FirebaseFirestore
import com.wayss.app.model.VerificationRequest
import kotlinx.coroutines.tasks.await

class FirestoreRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun submitVerification(request: VerificationRequest) {
        db.collection("verificationRequests")
            .document(request.id)
            .set(request)
            .await()
    }
}
