package com.example.randomnotifier

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.widget.Toast
import java.io.IOException

class MediaManager(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var isRecording = false
    private var isPaused = false
    private val fileName = "${context.externalCacheDir?.absolutePath}/audiorecordtest.3gp"

    fun startRecording() {
        if (isPaused) {
            // Continue recording from the pause state
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mediaRecorder?.resume()
                isRecording = true
                isPaused = false
                showToast("Recording resumed")
            } else {
                showToast("Resume not supported on this device")
            }
        } else {
            // Start a new recording
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(fileName)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

                try {
                    prepare()
                    start()
                    isRecording = true
                    showToast("Recording started")
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        val curRecording = isRecording
        isRecording = false
        isPaused = false
        if(curRecording) {
            showToast("Recording stopped")
        }
    }

    fun pauseRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mediaRecorder?.pause()
            isPaused = true
            showToast("Recording paused")
        } else {
            showToast("Pause not supported on this device")
        }
    }

    fun resumeRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mediaRecorder?.resume()
            isPaused = false
            showToast("Recording resumed")
        } else {
            showToast("Resume not supported on this device")
        }
    }

    fun playLastRecording(onCompletion: () -> Unit) {
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                start()
                showToast("Playing recording")
                setOnCompletionListener {
                    showToast("Playback completed")
                    release()
                    onCompletion()
                    mediaPlayer = null
                }
            } catch (e: IOException) {
                e.printStackTrace()
                showToast("Failed to play recording")
            }
        }
    }

    fun stopPlayback() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
                it.reset()
                it.release()
                mediaPlayer = null
                showToast("Playback stopped")
            }
        }
    }

    fun isPlaying(): Boolean {
        mediaPlayer?.let {
            return it.isPlaying
        }
        return false
    }

    fun saveRecording() {
        // TODO
        // Save the recording to a permanent location
        // Implement the save functionality as needed
        showToast("Recording saved")
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
