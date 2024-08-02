package com.example.randomnotifier

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.NumberPicker
import android.os.Handler
import android.os.Looper

class CustomNumberPicker(context: Context, attrs: AttributeSet?) : NumberPicker(context, attrs) {

    private var onValueChangedListener: OnValueChangeListener? = null
    private val handler = Handler(Looper.getMainLooper())

    init {
        setNumberPickerTextSize(20f)
        setOnValueChangedListener(null)
    }

    override fun setOnValueChangedListener(listener: OnValueChangeListener?) {
        onValueChangedListener = listener
        super.setOnValueChangedListener { picker, oldVal, newVal ->
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({
                setNumberPickerTextSize(20f)
            }, 100) // 100msの遅延を設定
            onValueChangedListener?.onValueChange(picker, oldVal, newVal)
        }
    }

    private fun setNumberPickerTextSize(textSize: Float) {
        try {
            val count = this.childCount
            for (i in 0 until count) {
                val child = this.getChildAt(i)
                if (child is EditText) {
                    child.textSize = textSize
                    println(child.textSize)
                    child.setTypeface(child.typeface, Typeface.BOLD)
                    child.visibility = View.VISIBLE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
