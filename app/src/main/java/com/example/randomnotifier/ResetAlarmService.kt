package com.example.randomnotifier

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class ResetAlarmService: Service() {

    override fun onCreate() {
        super.onCreate()

        // TODO it needs permision
        // val notification = Notification.Builder(this, "default")
        //     .setContentTitle("Think NOW! start running")
        //     .setContentText("Think NOW! start running in the foreground")
        //     .setSmallIcon(R.drawable.ic_notification)
        //     .build()

        // startForeground(1, notification)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        println("onTaskRemoved called")

        if(!DataManager.isInUse()) {
            DataManager.init(this)
        }
        DataManager.scheduleNextNotification(this)

        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
