package com.example.randomnotifier

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.EditText
import android.widget.NumberPicker

class CustomNumberPicker(context: Context, attrs: AttributeSet?) : NumberPicker(context, attrs) {

    private var onValueChangedListener: OnValueChangeListener? = null

    init {
        setNumberPickerTextSize(25f)
        setOnValueChangedListener(null)
    }

    override fun setOnValueChangedListener(listener: OnValueChangeListener?) {
        onValueChangedListener = listener
        super.setOnValueChangedListener { picker, oldVal, newVal ->
            setNumberPickerTextSize(25f)
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
                    child.setTypeface(child.typeface, Typeface.BOLD)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
