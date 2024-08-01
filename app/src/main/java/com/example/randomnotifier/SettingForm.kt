package com.example.randomnotifier

import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import android.os.Bundle
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
        fileNameView.text = DataManager.getFilePath()

        val notifyTime1Hour = DataManager.getNotifyTime1Hour()
        val notifyTime1Min = DataManager.getNotifyTime1Minute()
        val alarm1View: TextView = findViewById(R.id.alarm1_view)
        alarm1View.text = String.format("%02d:%02d", notifyTime1Hour, notifyTime1Min)
        val alarm1Switch: SwitchCompat = findViewById(R.id.alarm1_switch)
        alarm1Switch.isChecked = DataManager.isNotifyTime1En()

        val notifyTime2Hour = DataManager.getNotifyTime2Hour()
        val notifyTime2Min = DataManager.getNotifyTime2Minute()
        val alarm2View: TextView = findViewById(R.id.alarm2_view)
        alarm2View.text = String.format("%02d:%02d", notifyTime2Hour, notifyTime2Min)
        val alarm2Switch: SwitchCompat = findViewById(R.id.alarm2_switch)
        alarm2Switch.isChecked = DataManager.isNotifyTime2En()

        val notifyTime3Hour = DataManager.getNotifyTime3Hour()
        val notifyTime3Min = DataManager.getNotifyTime3Minute()
        val alarm3View: TextView = findViewById(R.id.alarm3_view)
        alarm3View.text = String.format("%02d:%02d", notifyTime3Hour, notifyTime3Min)
        val alarm3Switch: SwitchCompat = findViewById(R.id.alarm3_switch)
        alarm3Switch.isChecked = DataManager.isNotifyTime3En()
    }
}
