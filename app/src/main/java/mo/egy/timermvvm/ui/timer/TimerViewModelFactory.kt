package mo.egy.timermvvm.ui.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.Dispatchers
import mo.egy.timermvvm.data.alarm.AlarmUtil
import mo.egy.timermvvm.data.notification.NotificationUtil
import mo.egy.timermvvm.data.preference.PrefUtil

class TimerViewModelFactory(
    private val prefUtil: PrefUtil,
    private val notificationUtil: NotificationUtil,
    private val alarmUtil: AlarmUtil,

    ) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TimerVIewModel(
            prefUtil,
            notificationUtil,
            alarmUtil,
            Dispatchers.Main
        ) as T
    }

}