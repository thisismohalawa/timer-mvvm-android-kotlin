package mo.egy.timermvvm.data.alarm

import android.content.Context

interface AlarmUtil {

    fun setAlarm(context: Context, nowSeconds: Long, secondsRemaining: Long): Long

    fun removeAlarm(context: Context)

}