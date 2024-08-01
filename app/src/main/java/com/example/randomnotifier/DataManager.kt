package com.example.randomnotifier

import java.util.*
import android.content.Intent
import android.app.PendingIntent
import android.content.Context
import android.content.SharedPreferences
import android.app.AlarmManager

object DataManager {
    private var filePath: String? = ""
    private var notifyTime1En = false
    private var notifyTime2En = false
    private var notifyTime3En = false
    private var notifyTime1Hour = 6
    private var notifyTime1Minute = 30
    private var notifyTime2Hour = 11
    private var notifyTime2Minute = 30
    private var notifyTime3Hour = 20
    private var notifyTime3Minute = 0
    private var answerTime: Int = 1 * 60

    private lateinit var sharedPreferences: SharedPreferences
    private val KEY_QUESTION_FILE = "filePath"
    private val KEY_NOTIFYTIME1_ENABLE = "notifyTime1Enable"
    private val KEY_NOTIFYTIME1_HOUR = "notifyTime1Hour"
    private val KEY_NOTIFYTIME1_MIN = "notifyTime1Minute"
    private val KEY_NOTIFYTIME2_ENABLE = "notifyTime2Enable"
    private val KEY_NOTIFYTIME2_HOUR = "notifyTime2Hour"
    private val KEY_NOTIFYTIME2_MIN = "notifyTime2Minute"
    private val KEY_NOTIFYTIME3_ENABLE = "notifyTime3Enable"
    private val KEY_NOTIFYTIME3_HOUR = "notifyTime3Hour"
    private val KEY_NOTIFYTIME3_MIN = "notifyTime3Minute"
    private val KEY_ANSWER_TIME = "answerTime"

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        loadSettingData()
    }

    fun loadSettingData() {
        filePath = getSettingString(KEY_QUESTION_FILE, "")

        // 通知時間1 仮に6:30
        notifyTime1En = getSettingBoolean(KEY_NOTIFYTIME1_ENABLE, true)
        notifyTime1Hour = getSettingInt(KEY_NOTIFYTIME1_HOUR, 6)
        notifyTime1Minute = getSettingInt(KEY_NOTIFYTIME1_MIN, 30)

        // 通知時間2 仮に11:30
        notifyTime2En = getSettingBoolean(KEY_NOTIFYTIME2_ENABLE, true)
        notifyTime2Hour = getSettingInt(KEY_NOTIFYTIME2_HOUR, 11)
        notifyTime2Minute = getSettingInt(KEY_NOTIFYTIME2_MIN, 30)

        // 通知時間3 仮に20:00
        notifyTime3En = getSettingBoolean(KEY_NOTIFYTIME3_ENABLE, true)
        notifyTime3Hour = getSettingInt(KEY_NOTIFYTIME3_HOUR, 20)
        notifyTime3Minute = getSettingInt(KEY_NOTIFYTIME3_MIN, 0)

        answerTime = getSettingInt(KEY_ANSWER_TIME, 1*60)
    }

    fun saveSettingData() {
        // TODO
        filePath?.let {
            saveSetting(KEY_QUESTION_FILE, it)
        }

        saveSetting(KEY_NOTIFYTIME1_ENABLE, notifyTime1En)
        saveSetting(KEY_NOTIFYTIME1_HOUR, notifyTime1Hour)
        saveSetting(KEY_NOTIFYTIME1_MIN, notifyTime1Minute)
        saveSetting(KEY_NOTIFYTIME2_ENABLE, notifyTime2En)
        saveSetting(KEY_NOTIFYTIME2_HOUR, notifyTime2Hour)
        saveSetting(KEY_NOTIFYTIME2_MIN, notifyTime2Minute)
        saveSetting(KEY_NOTIFYTIME3_ENABLE, notifyTime3En)
        saveSetting(KEY_NOTIFYTIME3_HOUR, notifyTime3Hour)
        saveSetting(KEY_NOTIFYTIME3_MIN, notifyTime3Minute)

        saveSetting(KEY_ANSWER_TIME, answerTime)
    }

    // Getter and Setter for filePath
    fun getFilePath(): String? {
        return filePath
    }

    fun setFilePath(value: String) {
        filePath = value
    }

    // Getter and Setter for notifyTime1En
    fun isNotifyTime1En(): Boolean {
        return notifyTime1En
    }

    fun setNotifyTime1En(value: Boolean) {
        notifyTime1En = value
    }

    // Getter and Setter for notifyTime1
    fun getNotifyTime1Hour(): Int {
        return notifyTime1Hour
    }

    fun setNotifyTime1Hour(value: Int) {
        notifyTime1Hour = value
    }

    fun getNotifyTime1Minute(): Int {
        return notifyTime1Minute
    }

    fun setNotifyTime1Minute(value: Int) {
        notifyTime1Minute = value
    }

    // Getter and Setter for notifyTime2En
    fun isNotifyTime2En(): Boolean {
        return notifyTime2En
    }

    fun setNotifyTime2En(value: Boolean) {
        notifyTime2En = value
    }

    // Getter and Setter for notifyTime2
    fun getNotifyTime2Hour(): Int {
        return notifyTime2Hour
    }

    fun setNotifyTime2Hour(value: Int) {
        notifyTime2Hour = value
    }

    fun getNotifyTime2Minute(): Int {
        return notifyTime2Minute
    }

    fun setNotifyTime2Minute(value: Int) {
        notifyTime2Minute = value
    }

    // Getter and Setter for notifyTime3En
    fun isNotifyTime3En(): Boolean {
        return notifyTime3En
    }

    fun setNotifyTime3En(value: Boolean) {
        notifyTime3En = value
    }

    // Getter and Setter for notifyTime3
    fun getNotifyTime3Hour(): Int {
        return notifyTime3Hour
    }

    fun setNotifyTime3Hour(value: Int) {
        notifyTime3Hour = value
    }

    fun getNotifyTime3Minute(): Int {
        return notifyTime3Minute
    }

    fun setNotifyTime3Minute(value: Int) {
        notifyTime3Minute = value
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

    // wrapper of sharedPreferences
    fun saveSetting(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun saveSetting(key: String, value: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun saveSetting(key: String, value: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun saveSetting(key: String, value: Long) {
        val editor = sharedPreferences.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun getSettingString(key: String, defaultValue: String): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    fun getSettingInt(key: String, defaultValue: Int): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    fun getSettingBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun getSettingLong(key: String, defaultValue: Long): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }
}
