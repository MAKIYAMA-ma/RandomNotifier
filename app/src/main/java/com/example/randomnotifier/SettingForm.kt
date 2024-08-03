package com.example.randomnotifier

import android.app.TimePickerDialog
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.TimePicker
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import java.util.*

class SettingForm : AppCompatActivity() {
    var fileUri :String = ""
    var notifyTime1Hour = DataManager.getNotifyTime1Hour()
    var notifyTime1Min = DataManager.getNotifyTime1Minute()
    var notifyTime2Hour = DataManager.getNotifyTime2Hour()
    var notifyTime2Min = DataManager.getNotifyTime2Minute()
    var notifyTime3Hour = DataManager.getNotifyTime3Hour()
    var notifyTime3Min = DataManager.getNotifyTime3Minute()

    private lateinit var selectFileLauncher: ActivityResultLauncher<Intent>

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
        DataManager.getFilePath()?.let {
            fileUri = it
        }
        println(fileUri)
        fileNameView.text = getFileName(Uri.parse(fileUri))

        // selectFileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        //     uri?.let {
        //         // テキストファイルの内容をTextViewに表示
        //         // val fileContent = readTextFromUri(uri)
        //         // fileNameView.text = fileContent
        //         fileUri = uri.toString()
        //         fileNameView.text = getFileName(uri)
        //     }
        // }
        selectFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let { uri ->
                    // val fileContent = readTextFromUri(uri)
                    // fileNameView.text = fileContent
                    fileUri = uri.toString()
                    fileNameView.text = getFileName(uri)
                }
            }
        }

        fileNameView.setOnClickListener {
            openFileSelector()
        }

        val alarm1View: TextView = findViewById(R.id.alarm1_view)
        alarm1View.text = String.format("%02d:%02d", notifyTime1Hour, notifyTime1Min)
        val alarm1Switch: SwitchCompat = findViewById(R.id.alarm1_switch)
        alarm1Switch.isChecked = DataManager.isNotifyTime1En()

        val alarm2View: TextView = findViewById(R.id.alarm2_view)
        alarm2View.text = String.format("%02d:%02d", notifyTime2Hour, notifyTime2Min)
        val alarm2Switch: SwitchCompat = findViewById(R.id.alarm2_switch)
        alarm2Switch.isChecked = DataManager.isNotifyTime2En()

        val alarm3View: TextView = findViewById(R.id.alarm3_view)
        alarm3View.text = String.format("%02d:%02d", notifyTime3Hour, notifyTime3Min)
        val alarm3Switch: SwitchCompat = findViewById(R.id.alarm3_switch)
        alarm3Switch.isChecked = DataManager.isNotifyTime3En()

        alarm1View.setOnClickListener {
            // 現在の時間を取得
            val calendar = Calendar.getInstance()

            // TimePickerDialogを作成して表示
            val timePickerDialog = TimePickerDialog(this, object : TimePickerDialog.OnTimeSetListener {
                override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
                    notifyTime1Hour = hourOfDay
                    notifyTime1Min = minute
                    alarm1View.text = String.format("%02d:%02d", hourOfDay, minute)
                }
            }, notifyTime1Hour, notifyTime1Min, true) // trueは24時間形式、falseはAM/PM形式

            timePickerDialog.show()
        }
        alarm2View.setOnClickListener {
            // 現在の時間を取得
            val calendar = Calendar.getInstance()

            // TimePickerDialogを作成して表示
            val timePickerDialog = TimePickerDialog(this, object : TimePickerDialog.OnTimeSetListener {
                override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
                    notifyTime2Hour = hourOfDay
                    notifyTime2Min = minute
                    alarm2View.text = String.format("%02d:%02d", hourOfDay, minute)
                }
            }, notifyTime2Hour, notifyTime2Min, true) // trueは24時間形式、falseはAM/PM形式

            timePickerDialog.show()
        }
        alarm3View.setOnClickListener {
            // 現在の時間を取得
            val calendar = Calendar.getInstance()

            // TimePickerDialogを作成して表示
            val timePickerDialog = TimePickerDialog(this, object : TimePickerDialog.OnTimeSetListener {
                override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
                    notifyTime3Hour = hourOfDay
                    notifyTime3Min = minute
                    alarm3View.text = String.format("%02d:%02d", hourOfDay, minute)
                }
            }, notifyTime3Hour, notifyTime3Min, true) // trueは24時間形式、falseはAM/PM形式

            timePickerDialog.show()
        }

        val minutePicker: NumberPicker = findViewById(R.id.answertime_minute_picker)
        val secondPicker: NumberPicker = findViewById(R.id.answertime_second_picker)

        minutePicker.minValue = 0
        minutePicker.maxValue = 59
        secondPicker.minValue = 0
        secondPicker.maxValue = 59

        minutePicker.setFormatter { String.format("%02d", it) }
        secondPicker.setFormatter { String.format("%02d", it) }
        val anserTime = DataManager.getAnswerTime()

        minutePicker.wrapSelectorWheel = true
        secondPicker.wrapSelectorWheel = true

        minutePicker.value = anserTime / 60
        secondPicker.value = anserTime % 60

        val btSave = findViewById<Button>(R.id.save_button)
        val btSaveListener = SaveButtonListener()
        btSave.setOnClickListener(btSaveListener)
    }

    private fun resetAlarm() {
        DataManager.scheduleNextNotification(this)
    }

    private fun openFileSelector() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"
        }
        selectFileLauncher.launch(intent)
    }

    private fun getFileName(uri: Uri): String? {
        var fileName: String? = null
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }

    // private fun readTextFromUri(uri: Uri): String {
    //     val inputStream = contentResolver.openInputStream(uri)
    //     val reader = BufferedReader(InputStreamReader(inputStream))
    //     val stringBuilder = StringBuilder()
    //     reader.use {
    //         var line = it.readLine()
    //         while (line != null) {
    //             stringBuilder.append(line).append('\n')
    //             line = it.readLine()
    //         }
    //     }
    //     return stringBuilder.toString()
    // }

    private inner class SaveButtonListener : View.OnClickListener {
        override fun onClick(view: View) {
            DataManager.setFilePath(fileUri)

            val alarm1Switch: SwitchCompat = findViewById(R.id.alarm1_switch)
            DataManager.setNotifyTime1En(alarm1Switch.isChecked)
            DataManager.setNotifyTime1Hour(notifyTime1Hour)
            DataManager.setNotifyTime1Minute(notifyTime1Min)

            val alarm2Switch: SwitchCompat = findViewById(R.id.alarm2_switch)
            DataManager.setNotifyTime2En(alarm2Switch.isChecked)
            DataManager.setNotifyTime2Hour(notifyTime2Hour)
            DataManager.setNotifyTime2Minute(notifyTime2Min)

            val alarm3Switch: SwitchCompat = findViewById(R.id.alarm3_switch)
            DataManager.setNotifyTime3En(alarm3Switch.isChecked)
            DataManager.setNotifyTime3Hour(notifyTime3Hour)
            DataManager.setNotifyTime3Minute(notifyTime3Min)

            val minutePicker: NumberPicker = findViewById(R.id.answertime_minute_picker)
            val secondPicker: NumberPicker = findViewById(R.id.answertime_second_picker)
            val answerTime = minutePicker.value * 60 + secondPicker.value
            DataManager.setAnswerTime(answerTime)

            DataManager.saveSettingData()
            resetAlarm()
        }
    }
}
