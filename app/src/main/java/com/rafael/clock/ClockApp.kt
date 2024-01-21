package com.rafael.clock

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.content.Context
import android.os.Build
import com.rafael.clock.Constants.Companion.STOPWATCH_CHANNEL_ID
import com.rafael.clock.Constants.Companion.STOPWATCH_CHANNEL_NAME

class ClockApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                STOPWATCH_CHANNEL_ID,
                STOPWATCH_CHANNEL_NAME,
                IMPORTANCE_LOW
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}