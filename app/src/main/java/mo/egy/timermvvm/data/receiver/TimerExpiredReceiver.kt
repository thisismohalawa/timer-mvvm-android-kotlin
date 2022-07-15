package mo.egy.timermvvm.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import mo.egy.timermvvm.common.TimerState
import mo.egy.timermvvm.data.notification.NotificationUtilImpl
import mo.egy.timermvvm.data.preference.PrefUtilImpl

class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if (context != null) {

            val prefUtilImpl = PrefUtilImpl()

            prefUtilImpl.apply {

                if (isNotificationAllowed(context))
                    NotificationUtilImpl().showTimerExpired(context)

                setTimerState(TimerState.Stopped, context)
                setAlarmSetTime(0, context)
            }
        }
    }
}