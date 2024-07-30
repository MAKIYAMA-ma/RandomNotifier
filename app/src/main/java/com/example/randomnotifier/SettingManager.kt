package com.example.randomnotifier

import java.util.*

object SettingManager {
    var filePath = ""
    var notifyTime1 = Calendar.getInstance()
    var notifyTime2En = false
    var notifyTime2 = Calendar.getInstance()
    var notifyTime3En = false
    var notifyTime3 = Calendar.getInstance()
    var answerTime = 1 * 60

    fun init() {
        // 通知時間1 仮に6:30
        notifyTime1 = Calendar.getInstance()
        notifyTime1.set(Calendar.HOUR_OF_DAY, 6)
        notifyTime1.set(Calendar.MINUTE, 39)
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

    private fun loadSettingData() {
        // TODO
        // 設定ファイルを読む
    }

    private fun saveSettingData() {
        // TODO
        // 設定ファイルに保存する
    }

    fun getNotificationTime(): Calendar {
        val cur_calendar = Calendar.getInstance()
        cur_calendar.add(Calendar.SECOND, 5)

        if (notifyTime1.before(cur_calendar)) {
            notifyTime1.add(Calendar.DAY_OF_MONTH, 1)
        }
        if (notifyTime2.before(cur_calendar)) {
            notifyTime2.add(Calendar.DAY_OF_MONTH, 1)
        }
        if (notifyTime3.before(cur_calendar)) {
            notifyTime3.add(Calendar.DAY_OF_MONTH, 1)
        }

        return listOf(notifyTime1, notifyTime2, notifyTime3).minByOrNull { it.timeInMillis }!!
    }
}
