package com.example.randomnotifier

import android.widget.Button
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
    }
}
