package com.wayss.app.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class MediaUploadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        // Production upload orchestration belongs here:
        // validate -> compress -> upload -> persist Firestore metadata -> notify UI.
        // A worker is preferable to pretending a foreground upload is durable.
        return Result.success()
    }
}
