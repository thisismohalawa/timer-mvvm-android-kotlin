package mo.egy.timermvvm.data.notification

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import mo.egy.timermvvm.R
import mo.egy.timermvvm.common.AppConstants
import mo.egy.timermvvm.data.receiver.TimerNotificationActionReceiver
import mo.egy.timermvvm.ui.MainActivity
import java.text.SimpleDateFormat
import java.util.*


private const val CHANNEL_ID_TIMER = "timer"
private const val CHANNEL_NAME_TIMER = "Timer_app"
private const val TIMER_ID = 0

class NotificationUtilImpl : NotificationUtil {

    @SuppressLint("UnspecifiedImmutableFlag", "PrivateResource")
    override fun showTimerExpired(context: Context) {
        val startIntent = Intent(context, TimerNotificationActionReceiver::class.java)
        startIntent.action = AppConstants.ACTION_START
        val startPendingIntent =
            PendingIntent.getBroadcast(context, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT)


        val nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true)
        nBuilder.setContentTitle("Timer Expired!")
            .setContentText("Start again?")
            .setContentIntent(getPendingIntentWithStack(context, MainActivity::class.java))
            .addAction(
                R.drawable.ic_settings,
                "Start", startPendingIntent
            )

        val nManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true)

        nManager.notify(TIMER_ID, nBuilder.build())
    }

    @SuppressLint("UnspecifiedImmutableFlag", "PrivateResource")
    override fun showTimerRunning(context: Context, wakeUpTime: Long) {
        val stopIntent = Intent(context, TimerNotificationActionReceiver::class.java)
        stopIntent.action = AppConstants.ACTION_STOP
        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val pauseIntent = Intent(context, TimerNotificationActionReceiver::class.java)
        pauseIntent.action = AppConstants.ACTION_PAUSE
        val pausePendingIntent = PendingIntent.getBroadcast(
            context,
            0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val df = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)

        val nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true)
        nBuilder.setContentTitle("Timer is Running...")
            .setContentText("End: ${df.format(Date(wakeUpTime))}")
            .setContentIntent(getPendingIntentWithStack(context, MainActivity::class.java))
            .setOngoing(true)
            .addAction(
                R.drawable.ic_settings,
                "Stop",
                stopPendingIntent
            )
            .addAction(R.drawable.ic_settings, "Pause", pausePendingIntent)

        val nManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true)

        nManager.notify(TIMER_ID, nBuilder.build())
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun showTimerPaused(context: Context) {
        val resumeIntent = Intent(context, TimerNotificationActionReceiver::class.java)
        resumeIntent.action = AppConstants.ACTION_RESUME
        val resumePendingIntent = PendingIntent.getBroadcast(
            context,
            0, resumeIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true)
        nBuilder.setContentTitle("Timer is paused.")
            .setContentText("Resume?")
            .setContentIntent(getPendingIntentWithStack(context, MainActivity::class.java))
            .setOngoing(true)
            .addAction(R.drawable.ic_settings, "Resume", resumePendingIntent)


        val nManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true)

        nManager.notify(TIMER_ID, nBuilder.build())
    }

    override fun hideTimerNotification(context: Context) {
        val nManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nManager.cancel(TIMER_ID)
    }


    /*
    *
    * other
    * */
    private fun getBasicNotificationBuilder(
        context: Context,
        channelId: String,
        playSound: Boolean
    )
            : NotificationCompat.Builder {
        val notificationSound: Uri =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val nBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_settings)
            .setAutoCancel(true)
            .setDefaults(0)
        if (playSound) nBuilder.setSound(notificationSound)
        return nBuilder
    }

    private fun <T> getPendingIntentWithStack(
        context: Context,
        javaClass: Class<T>
    ): PendingIntent {
        val resultIntent = Intent(context, javaClass)
        resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addParentStack(javaClass)
        stackBuilder.addNextIntent(resultIntent)

        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    @TargetApi(26)
    private fun NotificationManager.createNotificationChannel(
        channelID: String,
        channelName: String,
        playSound: Boolean
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelImportance = if (playSound) NotificationManager.IMPORTANCE_DEFAULT
            else NotificationManager.IMPORTANCE_LOW
            val nChannel = NotificationChannel(channelID, channelName, channelImportance)
            nChannel.enableLights(true)
            nChannel.lightColor = Color.BLUE
            this.createNotificationChannel(nChannel)
        }
    }
}