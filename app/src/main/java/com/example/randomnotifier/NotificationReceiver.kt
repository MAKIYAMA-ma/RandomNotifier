package com.example.randomnotifier

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.text.SimpleDateFormat
import java.util.Calendar

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // intent to boot APP
        val resultIntent = Intent(context, MainActivity::class.java)
        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addNextIntentWithParentStack(resultIntent)
        val resultPendingIntent: PendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // for debug
        val cur_calendar = Calendar.getInstance()
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val titleStr = format.format(cur_calendar.time)

        if(!DataManager.isInUse()) {
            DataManager.init(context)
        }

        // ランダムに問題を更新
        DataManager.updateQuestion(context)
        DataManager.saveSettingData()

        val notification = NotificationCompat.Builder(context, "default")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(titleStr)
            .setContentText(DataManager.getQuestion())
            .setContentIntent(resultPendingIntent) // 通知をタップしたときのインテントを設定
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // 通知をタップした後に自動で消す
            .build()

        // アラームが発動したら、次のアラームを仕掛ける
        DataManager.scheduleNextNotification(context)
        println("Receive")
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1, notification)
    }
}
