package com.rafael.clock

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rafael.clock.Constants.Companion.ACTION_RESET_SERVICE
import com.rafael.clock.Constants.Companion.ACTION_RESUME_SERVICE
import com.rafael.clock.Constants.Companion.ACTION_START_SERVICE
import com.rafael.clock.Constants.Companion.ACTION_STOP_SERVICE
import com.rafael.clock.Constants.Companion.NOTIFICATION_ID
import com.rafael.clock.Constants.Companion.STOPWATCH_CHANNEL_ID
import com.rafael.clock.Constants.States
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StopwatchService : Service() {

    private var job: Job? = null
    private var startTime: Long = 0
    private var elapsedTime: Long = 0
    private lateinit var notificationBuilder: NotificationCompat.Builder

    companion object {
        private val _formattedTime = MutableLiveData<String>()
        val formattedTime: LiveData<String> = _formattedTime

        private val _notificationFormattedTime = MutableLiveData<String>()
        val notificationFormattedTime: LiveData<String> = _notificationFormattedTime


        // Button State
        private val _state = MutableLiveData<States>()
        val state: LiveData<States> = _state

        var isLeftButtonEnabled: Int = -1
        var leftButtonTextResId: Int = 0
        var rightButtonTextResId: Int = 0
        var rightButtonColorResId: Int = 0
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_SERVICE -> {
                    startForegroundService()
                }

                ACTION_RESUME_SERVICE -> {
                    startTimer()
                }

                ACTION_STOP_SERVICE -> {
                    stopTimer()
                }

                ACTION_RESET_SERVICE -> {
                    resetTimer()
                }
            }
        }
        return START_NOT_STICKY
    }

    private fun startForegroundService() {
        startTimer()
        val notification: Notification = getNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun getNotification(): Notification {
        notificationBuilder = NotificationCompat.Builder(this, STOPWATCH_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stopwatch)
            .setContentTitle(getString(R.string.stopwatch))
            .setContentText(getString(R.string.initialTime))

        return notificationBuilder.build()
    }

    private fun startTimer() {
        _state.value = States.STARTED
        job = CoroutineScope(Dispatchers.Main).launch {
            runTimer()
        }
    }

    private fun stopTimer() {
        _state.value = States.STOPPED
        job?.cancel()
    }

    private fun resetTimer() {
        _state.value = States.INIT
        job?.cancel()
        _formattedTime.value = getString(R.string.fragmentFormat, 0, 0, 0)
        stopSelf()
    }

    private suspend fun runTimer() {
        startTime = System.currentTimeMillis() - elapsedTime
        while (true) {
            updateTimer()
            updateNotification()
            delay(10) // Update every 10 milliseconds
        }
    }

    private fun updateTimer() {
        elapsedTime = System.currentTimeMillis() - startTime

        val hours = (elapsedTime / (1000 * 60 * 60)).toInt()
        val minutes = ((elapsedTime / (1000 * 60)) % 60).toInt()
        val seconds = ((elapsedTime / 1000) % 60).toInt()
        val milliseconds = (elapsedTime % 1000) / 10

        _formattedTime.value = when {
            hours > 0 -> getString(R.string.fragmentFormat, hours, minutes, seconds)
            else -> getString(R.string.fragmentFormat, minutes, seconds, milliseconds)
        }

        _notificationFormattedTime.value =
            getString(R.string.notificationFormat, hours, minutes, seconds)
    }

    private fun updateNotification() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = notificationBuilder
            .setContentText(notificationFormattedTime.value)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}