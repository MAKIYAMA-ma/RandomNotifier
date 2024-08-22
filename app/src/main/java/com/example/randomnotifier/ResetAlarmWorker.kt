package com.example.randomnotifier

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class ResetAlarmWorker(appContext: Context, workerParams: WorkerParameters):
        Worker(appContext, workerParams) {

    override fun doWork(): Result {
        if(!DataManager.isInUse()) {
            DataManager.init(applicationContext)
        }
        DataManager.scheduleNextNotification(applicationContext)

        return Result.success()
    }
}
