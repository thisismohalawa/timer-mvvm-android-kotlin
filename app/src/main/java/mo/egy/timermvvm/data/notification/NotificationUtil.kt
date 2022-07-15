package mo.egy.timermvvm.data.notification

import android.content.Context

interface NotificationUtil {

    fun showTimerExpired(context: Context)
    fun showTimerRunning(context: Context, wakeUpTime: Long)
    fun showTimerPaused(context: Context)
    fun hideTimerNotification(context: Context)
}