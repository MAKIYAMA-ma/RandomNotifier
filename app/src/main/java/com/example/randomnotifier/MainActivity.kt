package com.example.randomnotifier

import android.Manifest
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlin.math.ceil

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_SCHEDULE_EXACT_ALARM = 1
    private val REQUEST_CODE_POST_NOTIFICATIONS= 2

    private val INTERVAL_MILLISECOND: Long = 200

    private var timer: CustomCountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(!DataManager.isInUse()) {
            DataManager.init(this)
        }

        createNotificationChannel()

        // 権限が許可されているか確認
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (alarmManager.canScheduleExactAlarms()) {
            // 権限が既に許可されている場合、アラームを設定する
            println("Already Permited")
            DataManager.scheduleNextNotification(this)
        } else {
            // 権限が許可されていない場合、要求する
            println("Need Permision")
            showPermissionDialog()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13以上かどうかをチェック
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // 権限が許可されていない場合、要求する
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_POST_NOTIFICATIONS
                )
            }
        }

        val questionView: TextView = findViewById(R.id.question_view)
        questionView.text = DataManager.getQuestion()

        setTimerBoxText(DataManager.getAnswerTime())

        val hintCb: CheckBox = findViewById(R.id.hint_cb)
        hintCb.setOnCheckedChangeListener { _, isChecked ->
            val hintView: TextView = findViewById(R.id.hint_box)
            if (isChecked) {
                hintView.text = DataManager.getHint()
            } else {
                hintView.text = ""
            }
        }

        // SETTINGボタンによる画面遷移
        val setting_button: Button = findViewById<Button>(R.id.setting_button)
        setting_button.setOnClickListener{
            val intent = Intent(this,SettingForm::class.java)
            startActivity(intent)
        }

        // set function of button
        val btStart = findViewById<Button>(R.id.start_button)
        val btStartListener = StartButtonListener()
        btStart.setOnClickListener(btStartListener)

        val btStop = findViewById<Button>(R.id.stop_button)
        val btStopListener = StopButtonListener()
        btStop.setOnClickListener(btStopListener)
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
                DataManager.scheduleNextNotification(this)
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

    private fun setTimerBoxText(second: Int) {
        val timerBox: TextView = findViewById(R.id.timer_box)
        if(second > 0) {
            timerBox.text = String.format("%02d:%02d", second / 60, second % 60)
        } else {
            timerBox.text = "FINISH!!"
        }
    }

    private inner class StartButtonListener : View.OnClickListener {
        override fun onClick(view: View) {
            timer?.cancel()
            setTimerBoxText(DataManager.getAnswerTime())

            val countDownMillisec: Long = DataManager.getAnswerTime().toLong() * 1000
            timer = CustomCountDownTimer(
                countDownMillisec,
                INTERVAL_MILLISECOND,
                onTickAction = { millisUntilFinished ->
                    setTimerBoxText(ceil(millisUntilFinished / 1000.0).toInt())
                },
                onFinishAction = {
                    setTimerBoxText(0)
                }
            )
            timer?.startNew()
            val btStop = findViewById<Button>(R.id.stop_button)
            btStop.text = getString(R.string.stop)
        }
    }

    private inner class StopButtonListener : View.OnClickListener {
        override fun onClick(view: View) {
            var paused = timer?.isPaused()
            if(paused == null) {
                paused = false
            }

            val btStop = findViewById<Button>(R.id.stop_button)
            if(paused) {
                timer?.resume()
                btStop.text = getString(R.string.stop)
            } else {
                timer?.pause()
                btStop.text = getString(R.string.resume)
            }
        }
    }
}
