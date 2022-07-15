package mo.egy.timermvvm.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import mo.egy.timermvvm.common.AppConstants
import mo.egy.timermvvm.common.TimerState
import mo.egy.timermvvm.data.alarm.AlarmUtilImpl
import mo.egy.timermvvm.data.notification.NotificationUtilImpl
import mo.egy.timermvvm.data.preference.PrefUtilImpl
import mo.egy.timermvvm.ui.timer.nowSeconds

class TimerNotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null)
            when (intent?.action) {
                /*
                *
                *
                * STOP
                * */
                AppConstants.ACTION_STOP -> {
                    AlarmUtilImpl().removeAlarm(context)
                    PrefUtilImpl().apply {
                        setAlarmSetTime(0, context)
                        setTimerState(TimerState.Stopped, context)
                    }
                    NotificationUtilImpl().hideTimerNotification(context)
                }
                /*
                *
                * PAUSE
                * */
                AppConstants.ACTION_PAUSE -> {
                    val prefUtilImpl = PrefUtilImpl()


                    var secondsRemaining = prefUtilImpl.getSecondsRemaining(context)
                    val alarmSetTime = prefUtilImpl.getAlarmSetTime(context)
                    val nowSeconds = nowSeconds

                    secondsRemaining -= nowSeconds - alarmSetTime


                    prefUtilImpl.apply {
                        setSecondsRemaining(secondsRemaining, context)

                        AlarmUtilImpl().removeAlarm(context)
                        setAlarmSetTime(0, context)
                        setTimerState(TimerState.Paused, context)

                        if (isNotificationAllowed(context))
                            NotificationUtilImpl().showTimerPaused(context)
                    }

                }

                /*
                *
                *
                * RESUME
                * */
                AppConstants.ACTION_RESUME -> {
                    val currentNowSeconds = nowSeconds
                    val secondsRemaining = PrefUtilImpl().getSecondsRemaining(context)
                    val wakeUpTime =
                        AlarmUtilImpl().setAlarm(context, currentNowSeconds, secondsRemaining)

                    PrefUtilImpl().apply {
                        setAlarmSetTime(currentNowSeconds, context)
                        setTimerState(TimerState.Running, context)

                        if (isNotificationAllowed(context))
                            NotificationUtilImpl().showTimerRunning(context, wakeUpTime)
                    }
                }

                /*
                *
                * START
                * */
                AppConstants.ACTION_START -> {
                    val currentMowSeconds = nowSeconds
                    val minutesRemaining = PrefUtilImpl().getTimerLength(context)
                    val secondsRemaining = minutesRemaining * 60L
                    val wakeUpTime =
                        AlarmUtilImpl().setAlarm(context, currentMowSeconds, secondsRemaining)

                    PrefUtilImpl().apply {
                        setAlarmSetTime(currentMowSeconds, context)
                        setTimerState(TimerState.Running, context)
                        setSecondsRemaining(secondsRemaining, context)

                        if (isNotificationAllowed(context))
                            NotificationUtilImpl().showTimerRunning(context, wakeUpTime)

                    }
                }
            }
    }

}