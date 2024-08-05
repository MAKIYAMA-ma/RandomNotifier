package com.example.randomnotifier

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

object DataManager {
    @Volatile private var isInUse = false

    private var filePath: String? = ""
    private var lineCount: Int = 0
    private var question: String = ""
    private var hint: String = ""

    private var notifyTime1En     = false
    private var notifyTime2En     = false
    private var notifyTime3En     = false
    private var notifyTime1Hour   = 6
    private var notifyTime1Minute = 30
    private var notifyTime2Hour   = 11
    private var notifyTime2Minute = 30
    private var notifyTime3Hour   = 20
    private var notifyTime3Minute = 0
    private var answerTime: Int   = 1 * 60

    private lateinit var sharedPreferences: SharedPreferences
    private val KEY_QUESTION_FILE      = "filePath"
    private val KEY_LINE_COUNT         = "lineCount"
    private val KEY_QEUSTION           = "question"
    private val KEY_HINT               = "hint"
    private val KEY_NOTIFYTIME1_ENABLE = "notifyTime1Enable"
    private val KEY_NOTIFYTIME1_HOUR   = "notifyTime1Hour"
    private val KEY_NOTIFYTIME1_MIN    = "notifyTime1Minute"
    private val KEY_NOTIFYTIME2_ENABLE = "notifyTime2Enable"
    private val KEY_NOTIFYTIME2_HOUR   = "notifyTime2Hour"
    private val KEY_NOTIFYTIME2_MIN    = "notifyTime2Minute"
    private val KEY_NOTIFYTIME3_ENABLE = "notifyTime3Enable"
    private val KEY_NOTIFYTIME3_HOUR   = "notifyTime3Hour"
    private val KEY_NOTIFYTIME3_MIN    = "notifyTime3Minute"
    private val KEY_ANSWER_TIME        = "answerTime"

    @Synchronized
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        loadSettingData()
        isInUse = true
    }

    @Synchronized
    fun isInUse(): Boolean {
        return isInUse
    }

    fun loadSettingData() {
        filePath = getSettingString(KEY_QUESTION_FILE, "")
        lineCount = getSettingInt(KEY_LINE_COUNT, 0)
        getSettingString(KEY_QEUSTION, "")?.let {
            question = it
        }
        getSettingString(KEY_HINT, "")?.let {
            hint = it
        }

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
        saveSetting(KEY_LINE_COUNT, lineCount)
        saveSetting(KEY_QEUSTION, question)
        saveSetting(KEY_HINT, hint)

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

    fun setFilePath(context: Context, value: String) {
        filePath = value

        // 同時に行数を更新する
        lineCount = getLineCountFromUri(context, Uri.parse(filePath))
    }

    fun updateQuestion(context: Context) {
        filePath?.let {
            if(lineCount > 0) {
                val selectedLine = (1..lineCount).random()
                val line = readLineFromUri(context, Uri.parse(it), selectedLine)
                val strs = line.split("	")
                println(strs)
                question = strs[0]
                if(strs.size > 1) {
                    hint = strs[1]
                } else {
                    hint = ""
                }
            }
        }
    }

    fun getQuestion(): String {
        return question
    }

    fun getHint(): String {
        return hint
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

    private fun getNotificationTime(): Calendar? {
        var nearestTime: Calendar? = null

        val notifyTime1 = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, notifyTime1Hour)
            set(Calendar.MINUTE, notifyTime1Minute)
            set(Calendar.SECOND, 0)
        }

        val notifyTime2 = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, notifyTime2Hour)
            set(Calendar.MINUTE, notifyTime2Minute)
            set(Calendar.SECOND, 0)
        }

        val notifyTime3 = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, notifyTime3Hour)
            set(Calendar.MINUTE, notifyTime3Minute)
            set(Calendar.SECOND, 0)
        }

        // 5秒後を基準に、既に今日は終わっている時刻は明日にずらす
        val curCalendar = Calendar.getInstance().apply {
            add(Calendar.SECOND, 5)
        }

        if (notifyTime1.before(curCalendar)) {
            notifyTime1.add(Calendar.DAY_OF_MONTH, 1)
        }
        if (notifyTime2.before(curCalendar)) {
            notifyTime2.add(Calendar.DAY_OF_MONTH, 1)
        }
        if (notifyTime3.before(curCalendar)) {
            notifyTime3.add(Calendar.DAY_OF_MONTH, 1)
        }

        // Enableのアラームの中から直近の日時を選ぶ
        if (notifyTime1En && (nearestTime == null || notifyTime1.before(nearestTime))) {
            nearestTime = notifyTime1
        }
        if (notifyTime2En && (nearestTime == null || notifyTime2.before(nearestTime))) {
            nearestTime = notifyTime2
        }
        if (notifyTime3En && (nearestTime == null || notifyTime3.before(nearestTime))) {
            nearestTime = notifyTime3
        }

        // Debug logging
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        println("Current time: ${format.format(curCalendar.time)}")
        println("Notify time 1: ${format.format(notifyTime1.time)} (enabled: $notifyTime1En)")
        println("Notify time 2: ${format.format(notifyTime2.time)} (enabled: $notifyTime2En)")
        println("Notify time 3: ${format.format(notifyTime3.time)} (enabled: $notifyTime3En)")
        println("Nearest time: ${nearestTime?.let { format.format(it.time) }}")

        return nearestTime

        // for debug
        // 10秒後を返す
        // val curCalendar = Calendar.getInstance()
        // curCalendar.add(Calendar.SECOND, 10)
        // return curCalendar
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

        // for debug
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        println(format.format(notificationTime.time))

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            notificationTime.timeInMillis,
            pendingIntent
        )
    }

    fun scheduleNextNotification(context: Context) {
        println(context)
        getNotificationTime()?.let {
            scheduleNotification(context, it)
        }
    }

    private fun getLineCountFromUri(context: Context, uri: Uri): Int {
        var lineCount = 0
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val reader = BufferedReader(InputStreamReader(inputStream))

            reader.use {
                while (it.readLine() != null) {
                    lineCount++
                }
            }
        } catch (e: FileNotFoundException) {
            Log.e("getLineCountFromUri", "File not found: ${e.message}")
        } catch (e: IOException) {
            Log.e("getLineCountFromUri", "IO error: ${e.message}")
        } catch (e: Exception) {
            Log.e("getLineCountFromUri", "Unexpected error: ${e.message}")
        }
        return lineCount
    }

    private fun readLineFromUri(context: Context, uri: Uri, lineNum: Int): String {
        var result = ""
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val reader = BufferedReader(InputStreamReader(inputStream))

            reader.use {
                var currentLineNum = 1
                var line = it.readLine()

                while (line != null) {
                    if (currentLineNum == lineNum) {
                        result = line
                        break
                    }
                    currentLineNum++
                    line = it.readLine()
                }
            }
        } catch (e: FileNotFoundException) {
            Log.e("getLineCountFromUri", "File not found: ${e.message}")
        } catch (e: IOException) {
            Log.e("getLineCountFromUri", "IO error: ${e.message}")
        } catch (e: Exception) {
            Log.e("getLineCountFromUri", "Unexpected error: ${e.message}")
        }
        return result
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

    fun getSettingString(key: String, defaultValue: String): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    fun getSettingInt(key: String, defaultValue: Int): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    fun getSettingBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }
}
