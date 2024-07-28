package com.example.randomnotifier

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationBuilder = NotificationCompat.Builder(context, "default")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("定期通知")
            .setContentText("これは指定された時間に送信された通知です")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1, notificationBuilder.build())
    }
}
