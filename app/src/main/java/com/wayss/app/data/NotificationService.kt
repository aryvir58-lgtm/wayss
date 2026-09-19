package com.wayss.app.data

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

class NotificationService {
    suspend fun token(): String = FirebaseMessaging.getInstance().token.await()
}
