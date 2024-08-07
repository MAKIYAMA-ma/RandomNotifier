package com.example.randomnotifier

import android.Manifest
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlin.math.ceil

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_SCHEDULE_EXACT_ALARM = 1
    private val REQUEST_CODE_OTHER_PERMISSIONS = 2

    private val INTERVAL_MILLISECOND: Long = 200

    private var timer: CustomCountDownTimer? = null

    private var recEnable = true

    private var mediaManager: MediaManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 最初に権限の確認と要求をする
        checkPermissions()

        mediaManager = MediaManager(this)

        if(!DataManager.isInUse()) {
            DataManager.init(this)
        }

        createNotificationChannel()

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

        // 問題更新機能
        val btNextQuestion: Button = findViewById<Button>(R.id.next_question_button)
        btNextQuestion.setOnClickListener{
            DataManager.updateQuestion(this)
            DataManager.saveSettingData()

            val questionView: TextView = findViewById(R.id.question_view)
            questionView.text = DataManager.getQuestion()

            val hintCb: CheckBox = findViewById(R.id.hint_cb)
            hintCb.isChecked = false
        }

        // SETTINGボタンによる画面遷移
        val btSetting: Button = findViewById<Button>(R.id.setting_button)
        btSetting.setOnClickListener{
            val intent = Intent(this,SettingForm::class.java)
            startActivity(intent)
        }

        // set function of button
        val btTimer = findViewById<ImageButton>(R.id.timer_button)
        val btTimerListener = TimerButtonListener()
        btTimer.setOnClickListener(btTimerListener)

        val btReset = findViewById<ImageButton>(R.id.reset_timer_button)
        val btResetListener = ResetButtonListener()
        btReset.setOnClickListener(btResetListener)

        val btStop = findViewById<ImageButton>(R.id.stop_timer_button)
        btStop.setOnClickListener{
            timer?.cancel()
            mediaManager?.stopRecording()
        }

        val btRec = findViewById<ImageButton>(R.id.rec_button)
        val btRecListener = RecButtonListener()
        btRec.setOnClickListener(btRecListener)

        val btPlay = findViewById<ImageButton>(R.id.play_button)
        btPlay.setOnClickListener{
            mediaManager?.playLastRecording()
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

    private fun checkPermissions(): Boolean {
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

        val recordPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        val storageWritePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val storageReadPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        var notificationPermission = PackageManager.PERMISSION_GRANTED

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13以上かどうかをチェック
            notificationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
        }

        val permissions = mutableListOf<String>()
        if (recordPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.RECORD_AUDIO)
        }
        if (storageWritePermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (storageReadPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (notificationPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        return if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), REQUEST_CODE_OTHER_PERMISSIONS)
            false
        } else {
            true
        }
    }

    private inner class TimerButtonListener : View.OnClickListener {
        override fun onClick(view: View) {
            if(timer == null) {
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
            }

            val running = timer?.isRunning() ?: false
            val paused = timer?.isPaused() ?: false

            val btTimer = findViewById<ImageButton>(R.id.timer_button)
            var newImage: Drawable? = null

            if(running) {
                if(paused) {
                    timer?.resume()
                    mediaManager?.resumeRecording()
                    newImage = ContextCompat.getDrawable(this@MainActivity, R.drawable.pause_button)
                } else {
                    timer?.pause()
                    mediaManager?.stopRecording()
                    newImage = ContextCompat.getDrawable(this@MainActivity, R.drawable.start_button)
                }
            } else {
                timer?.start()
                mediaManager?.startRecording()
                newImage = ContextCompat.getDrawable(this@MainActivity, R.drawable.pause_button)
            }
            newImage?.let {
                btTimer.setImageDrawable(it)
            }
        }
    }

    private inner class ResetButtonListener : View.OnClickListener {
        override fun onClick(view: View) {
            timer?.cancel()
            mediaManager?.stopRecording()
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
        }
    }

    private inner class RecButtonListener : View.OnClickListener {
        override fun onClick(view: View) {
            val btRec = findViewById<ImageButton>(R.id.rec_button)
            if(recEnable) {
                val newImage = ContextCompat.getDrawable(this@MainActivity, R.drawable.mike_disable_button)
                btRec.setImageDrawable(newImage)
                recEnable = false
            } else {
                val newImage = ContextCompat.getDrawable(this@MainActivity, R.drawable.mike_enable_button)
                btRec.setImageDrawable(newImage)
                recEnable = true
            }
        }
    }
}
