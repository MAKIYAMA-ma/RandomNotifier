package com.example.randomnotifier

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Button
import android.os.Bundle
import android.os.Build
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.NotificationChannel
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.provider.Settings
import java.util.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_SCHEDULE_EXACT_ALARM = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SettingManager.init()

        createNotificationChannel()

        // 権限が許可されているか確認
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        println(alarmManager.canScheduleExactAlarms())
        if (alarmManager.canScheduleExactAlarms()) {
            // 権限が既に許可されている場合、アラームを設定する
            println("Already Permited")
            SettingManager.scheduleNextNotification(this)
        } else {
            // 権限が許可されていない場合、要求する
            println("Need Permision")
            showPermissionDialog()
        }

        // SETTINGボタンによる画面遷移
        val setting_button: Button = findViewById<Button>(R.id.setting_button)
        setting_button.setOnClickListener{
            val intent = Intent(this,SettingForm::class.java)
            startActivity(intent)
        }
    }

    private fun showPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission required")
            .setMessage("To schedule exact alarms, please enable the permission in system settings.")
            .setPositiveButton("Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_SCHEDULE_EXACT_ALARM) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // 権限が許可された場合、アラームを設定する
                println("Permited")
                SettingManager.scheduleNextNotification(this)
            } else {
                // 権限が拒否された場合の処理
                // 必要に応じて対応
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Default Channel"
            val descriptionText = "This is the default notification channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("default", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
