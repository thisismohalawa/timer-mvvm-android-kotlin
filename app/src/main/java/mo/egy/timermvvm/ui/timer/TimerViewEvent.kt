package mo.egy.timermvvm.ui.timer

import android.content.Context

sealed class TimerViewEvent {
    // buttonsEvent
    object PausedTimer : TimerViewEvent()
    data class StartTimer(val context: Context?) : TimerViewEvent()
    data class CancelTimer(val context: Context?) : TimerViewEvent()
    // view
    data class OnViewResumed(val context: Context?) : TimerViewEvent()
    data class OnViewPaused(val context: Context?) : TimerViewEvent()

}
