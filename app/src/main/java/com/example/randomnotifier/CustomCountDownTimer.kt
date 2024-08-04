package com.example.randomnotifier

import android.os.CountDownTimer

class CustomCountDownTimer(
    private var millisInFuture: Long,
    private val countDownInterval: Long,
    private val onTickAction: (Long) -> Unit,
    private val onFinishAction: () -> Unit
) : CountDownTimer(millisInFuture, countDownInterval) {

    private var timeRemaining: Long = millisInFuture
    private var isPaused: Boolean = false

    override fun onTick(millisUntilFinished: Long) {
        if (!isPaused) {
            onTickAction(millisUntilFinished)
        }
    }

    override fun onFinish() {
        onFinishAction()
    }

    fun pause() {
        isPaused = true
        cancel()
    }

    fun resume() {
        isPaused = false
        // 残り時間で新しいタイマーを開始
        millisInFuture = timeRemaining
        start()
    }

    fun startNew() {
        cancel()
        timeRemaining = millisInFuture
        isPaused = false
        start()
    }

    fun isPaused(): Boolean {
        return isPaused
    }
}
