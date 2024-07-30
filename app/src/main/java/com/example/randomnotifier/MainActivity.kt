package com.example.randomnotifier

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Button
import android.os.Bundle
import android.os.Build
import android.app.AlarmManager
import android.app.PendingIntent
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

        createNotificationChannel()

        // 権限が許可されているか確認
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        println(alarmManager.canScheduleExactAlarms())
        if (alarmManager.canScheduleExactAlarms()) {
            // 権限が既に許可されている場合、アラームを設定する
            println("Already Permited")
            scheduleNotification(getNotificationTime())
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
                scheduleNotification(getNotificationTime())
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

    private fun scheduleNotification(notificationTime: Calendar) {
        val intent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // 既にアラームが存在するならいったん解除
        alarmManager.cancel(pendingIntent)

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            notificationTime.timeInMillis,
            pendingIntent
        )
    }

    private fun getNotificationTime(): Calendar {
        val cur_calendar = Calendar.getInstance()
        cur_calendar.add(Calendar.SECOND, 5)

        // 通知時間1 仮に6:30
        // TODO 設定内容取得
        val calendar1 = Calendar.getInstance()
        calendar1.set(Calendar.HOUR_OF_DAY, 6)
        calendar1.set(Calendar.MINUTE, 30)
        calendar1.set(Calendar.SECOND, 0)
        if (calendar1.before(cur_calendar)) {
            calendar1.add(Calendar.DAY_OF_MONTH, 1)
        }

        // 通知時間2 仮に11:30
        // TODO 設定内容取得
        val calendar2 = Calendar.getInstance()
        calendar2.set(Calendar.HOUR_OF_DAY, 11)
        calendar2.set(Calendar.MINUTE, 30)
        calendar2.set(Calendar.SECOND, 0)
        if (calendar2.before(cur_calendar)) {
            calendar2.add(Calendar.DAY_OF_MONTH, 1)
        }

        // 通知時間3 仮に20:00
        // TODO 設定内容取得
        val calendar3 = Calendar.getInstance()
        calendar3.set(Calendar.HOUR_OF_DAY, 20)
        calendar3.set(Calendar.MINUTE, 0)
        calendar3.set(Calendar.SECOND, 0)
        if (calendar3.before(cur_calendar)) {
            calendar3.add(Calendar.DAY_OF_MONTH, 1)
        }

        return listOf(calendar1, calendar2, calendar3).minByOrNull { it.timeInMillis }!!
    }
}
