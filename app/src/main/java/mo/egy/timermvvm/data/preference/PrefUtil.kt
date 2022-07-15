package mo.egy.timermvvm.data.preference

import android.content.Context
import mo.egy.timermvvm.common.TimerState

interface PrefUtil {

    // settingPreferences
    fun getTimerLength(context: Context): Int
    fun isNotificationAllowed(context: Context): Boolean

    // PreviousTimer
    fun getPreviousTimerLengthSeconds(context: Context): Long
    fun setPreviousTimerLengthSeconds(seconds: Long, context: Context)

    // timerState
    fun getTimerState(context: Context): TimerState
    fun setTimerState(state: TimerState, context: Context)

    // SecondsRemaining
    fun getSecondsRemaining(context: Context): Long
    fun setSecondsRemaining(seconds: Long, context: Context)

    // alarm
    fun getAlarmSetTime(context: Context): Long
    fun setAlarmSetTime(time: Long, context: Context)
}