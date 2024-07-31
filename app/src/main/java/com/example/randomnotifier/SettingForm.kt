package com.example.randomnotifier

import android.widget.Button
import android.widget.TextView
import android.os.Bundle
import android.os.Build
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.NotificationManager
import android.app.NotificationChannel
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class SettingForm : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_form)

        // RETURNボタンによる画面遷移
        val return_button: Button = findViewById<Button>(R.id.return_button)
        return_button.setOnClickListener{
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        // SettingManagerに基づいて各フォームの値を入れる
        val fileNameView: TextView = findViewById(R.id.file_name_view)
        fileNameView.text = SettingManager.getFilePath()

        val notifyTime1 = SettingManager.getNotifyTime1()
        val notifyTime1Hour = notifyTime1.get(Calendar.HOUR_OF_DAY)
        val notifyTime1Min = notifyTime1.get(Calendar.MINUTE)
        val alarm1View: TextView = findViewById(R.id.alarm1_view)
        alarm1View.text = String.format("%02d:%02d", notifyTime1Hour, notifyTime1Min)

        val notifyTime2 = SettingManager.getNotifyTime2()
        val notifyTime2Hour = notifyTime2.get(Calendar.HOUR_OF_DAY)
        val notifyTime2Min = notifyTime2.get(Calendar.MINUTE)
        val alarm2View: TextView = findViewById(R.id.alarm2_view)
        alarm2View.text = String.format("%02d:%02d", notifyTime2Hour, notifyTime2Min)

        val notifyTime3 = SettingManager.getNotifyTime3()
        val notifyTime3Hour = notifyTime3.get(Calendar.HOUR_OF_DAY)
        val notifyTime3Min = notifyTime3.get(Calendar.MINUTE)
        val alarm3View: TextView = findViewById(R.id.alarm3_view)
        alarm3View.text = String.format("%02d:%02d", notifyTime3Hour, notifyTime3Min)
    }
}
