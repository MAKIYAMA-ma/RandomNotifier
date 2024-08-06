package com.example.randomnotifier

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageButton

class PushImageButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.imageButtonStyle
) : AppCompatImageButton(context, attrs, defStyleAttr) {

    private var clickListener: OnClickListener? = null

    init {
        setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    scaleX = 0.95f
                    scaleY = 0.95f
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    scaleX = 1.0f
                    scaleY = 1.0f
                    if (event.action == MotionEvent.ACTION_UP) {
                        clickListener?.onClick(this)
                    }
                }
            }
            true
        }
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        clickListener = listener
    }
}
