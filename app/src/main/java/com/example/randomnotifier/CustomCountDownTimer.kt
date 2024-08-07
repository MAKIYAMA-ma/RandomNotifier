package com.example.randomnotifier

import android.os.CountDownTimer

class CustomCountDownTimer(
    private var millisInFuture: Long,
    private val countDownInterval: Long,
    private val onTickAction: (Long) -> Unit,
    private val onFinishAction: () -> Unit
) {

    private var countDownTimer: CountDownTimer? = null

    private var timeRemaining: Long = millisInFuture
    private var isRunning: Boolean = false
    private var isPaused: Boolean = false

    fun start() {
        startNewTimer(millisInFuture)
        isRunning = true
        isPaused = false
    }

    fun cancel() {
        isRunning = false
        isPaused = false
        countDownTimer?.cancel()
    }

    fun pause() {
        isPaused = true
        countDownTimer?.cancel()
    }

    fun resume() {
        startNewTimer(timeRemaining)
        isPaused = false
    }

    private fun startNewTimer(millis: Long) {
        countDownTimer = object : CountDownTimer(millis, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                onTickAction(millisUntilFinished)
                timeRemaining = millisUntilFinished
            }

            override fun onFinish() {
                onFinishAction()
            }
        }
        countDownTimer?.start()
    }

    fun isPaused(): Boolean {
        return isPaused
    }

    fun isRunning(): Boolean {
        return isRunning
    }
}
