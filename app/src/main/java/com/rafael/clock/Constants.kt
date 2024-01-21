package com.rafael.clock

class Constants {
    companion object {
        const val ACTION_START_SERVICE = "ACTION_START_SERVICE"
        const val ACTION_RESUME_SERVICE = "ACTION_RESUME_SERVICE"
        const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
        const val ACTION_RESET_SERVICE = "ACTION_RESET_SERVICE"
        const val ACTION_LAP_SERVICE = "ACTION_LAP_SERVICE"

        const val STOPWATCH_CHANNEL_ID = "stopwatch_channel"
        const val STOPWATCH_CHANNEL_NAME = "Clock Notifications"
        const val NOTIFICATION_ID = 1
    }

    enum class States {
        INIT, STARTED, STOPPED
    }
}