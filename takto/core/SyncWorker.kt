package com.takto.launcher.core

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

/**
 * SyncWorker: Wi-Fi Constrained Background Replication Engine
 * Triggers Syncthing / rclone background replication without draining mobile data or battery.
 */
class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Trigger local rclone / Syncthing sync intent or unix socket trigger
            // 100% offline-first; fails gracefully if no local sync daemon is active
            triggerSyncthingReplication()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    private fun triggerSyncthingReplication() {
        // Broadcasts local intent to Syncthing-Android or runs local CLI synchronization
        val intent = android.content.Intent("com.nutomic.syncthingandroid.action.SYNC")
        applicationContext.sendBroadcast(intent)
    }

    companion object {
        private const val SYNC_WORK_TAG = "takto_logseq_sync_worker"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED) // Wi-Fi only
                .setRequiresBatteryNotLow(true)
                .build()

            val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .addTag(SYNC_WORK_TAG)
                .build()

            WorkManager.getInstance(context).enqueue(syncRequest)
        }
    }
}
