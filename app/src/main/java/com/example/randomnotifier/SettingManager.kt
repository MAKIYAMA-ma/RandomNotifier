package com.example.randomnotifier

import java.util.*
import android.content.Intent
import android.app.PendingIntent
import android.content.Context
import android.app.AlarmManager

object SettingManager {
    private var filePath = ""
    private var notifyTime1 = Calendar.getInstance()
    private var notifyTime2En = false
    private var notifyTime2 = Calendar.getInstance()
    private var notifyTime3En = false
    private var notifyTime3 = Calendar.getInstance()
    private var answerTime = 1 * 60

    fun init() {
        // 通知時間1 仮に6:30
        notifyTime1 = Calendar.getInstance()
        notifyTime1.set(Calendar.HOUR_OF_DAY, 6)
        notifyTime1.set(Calendar.MINUTE, 30)
        notifyTime1.set(Calendar.SECOND, 0)

        // 通知時間2 仮に11:30
        notifyTime2 = Calendar.getInstance()
        notifyTime2.set(Calendar.HOUR_OF_DAY, 11)
        notifyTime2.set(Calendar.MINUTE, 30)
        notifyTime2.set(Calendar.SECOND, 0)

        // 通知時間3 仮に20:00
        notifyTime3 = Calendar.getInstance()
        notifyTime3.set(Calendar.HOUR_OF_DAY, 20)
        notifyTime3.set(Calendar.MINUTE, 0)
        notifyTime3.set(Calendar.SECOND, 0)
    }

    fun loadSettingData() {
        // TODO
        // 設定ファイルを読む
    }

    fun saveSettingData() {
        // TODO
        // 設定ファイルに保存する
    }

    // Getter and Setter for filePath
    fun getFilePath(): String {
        return filePath
    }

    fun setFilePath(value: String) {
        filePath = value
    }

    // Getter and Setter for notifyTime1
    fun getNotifyTime1(): Calendar {
        return notifyTime1
    }

    fun setNotifyTime1(value: Calendar) {
        notifyTime1 = value
    }

    // Getter and Setter for notifyTime2En
    fun isNotifyTime2En(): Boolean {
        return notifyTime2En
    }

    fun setNotifyTime2En(value: Boolean) {
        notifyTime2En = value
    }

    // Getter and Setter for notifyTime2
    fun getNotifyTime2(): Calendar {
        return notifyTime2
    }

    fun setNotifyTime2(value: Calendar) {
        notifyTime2 = value
    }

    // Getter and Setter for notifyTime3En
    fun isNotifyTime3En(): Boolean {
        return notifyTime3En
    }

    fun setNotifyTime3En(value: Boolean) {
        notifyTime3En = value
    }

    // Getter and Setter for notifyTime3
    fun getNotifyTime3(): Calendar {
        return notifyTime3
    }

    fun setNotifyTime3(value: Calendar) {
        notifyTime3 = value
    }

    // Getter and Setter for answerTime
    fun getAnswerTime(): Int {
        return answerTime
    }

    fun setAnswerTime(value: Int) {
        answerTime = value
    }

    private fun getNotificationTime(): Calendar {
        // val cur_calendar = Calendar.getInstance()
        // cur_calendar.add(Calendar.SECOND, 5)

        // if (notifyTime1.before(cur_calendar)) {
        //     notifyTime1.add(Calendar.DAY_OF_MONTH, 1)
        // }
        // if (notifyTime2.before(cur_calendar)) {
        //     notifyTime2.add(Calendar.DAY_OF_MONTH, 1)
        // }
        // if (notifyTime3.before(cur_calendar)) {
        //     notifyTime3.add(Calendar.DAY_OF_MONTH, 1)
        // }

        // return listOf(notifyTime1, notifyTime2, notifyTime3).minByOrNull { it.timeInMillis }!!

        // for debug
        // 10秒後を返す
        val cur_calendar = Calendar.getInstance()
        cur_calendar.add(Calendar.SECOND, 10)
        return cur_calendar
    }

    private fun scheduleNotification(context: Context, notificationTime: Calendar) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // 既にアラームが存在するならいったん解除
        alarmManager.cancel(pendingIntent)

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            notificationTime.timeInMillis,
            pendingIntent
        )
    }

    fun scheduleNextNotification(context: Context) {
        scheduleNotification(context, getNotificationTime())
    }
}
