package mo.egy.timermvvm.ui.timer

import android.content.Context
import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import mo.egy.timermvvm.common.TimerState
import mo.egy.timermvvm.data.alarm.AlarmUtil
import mo.egy.timermvvm.data.notification.NotificationUtil
import mo.egy.timermvvm.data.preference.PrefUtil
import mo.egy.timermvvm.data.preference.PrefUtilImpl
import mo.egy.timermvvm.ui.base.BaseViewModel
import java.util.*
import kotlin.coroutines.CoroutineContext

const val TAG = "MAIN_DEBUG_TAG"

val nowSeconds: Long
    get() = Calendar.getInstance().timeInMillis / 1000

class TimerVIewModel(
    private val prefUtil: PrefUtil,
    private val notificationUtil: NotificationUtil,
    private val alarmUtil: AlarmUtil,
    uiContext: CoroutineContext
) : BaseViewModel<TimerViewEvent>(uiContext) {

    private lateinit var timer: CountDownTimer
    private var timerLengthSeconds: Long = 0
    private var timerState = TimerState.Stopped
    private var secondsRemaining: Long = 0

    // viewStates
    internal val progressMaxValue = MutableLiveData<Int>()
    internal val progressValue = MutableLiveData<Int>()
    internal val timerCountDownTextValue = MutableLiveData<String>()
    internal val startButtonEnabledStatus = MutableLiveData<Boolean>()
    internal val stopButtonEnabledStatus = MutableLiveData<Boolean>()
    internal val pauseButtonEnabledStatus = MutableLiveData<Boolean>()


    override fun handleEvent(event: TimerViewEvent) {
        when (event) {
            // buttonsEvents
            is TimerViewEvent.PausedTimer -> pauseTimerMainLogic()
            is TimerViewEvent.StartTimer -> startTimerMainLogic(event.context)
            is TimerViewEvent.CancelTimer -> cancelTimerMainLogic(event.context)
            // view
            is TimerViewEvent.OnViewPaused -> onViewPaused(event.context)
            is TimerViewEvent.OnViewResumed -> initTimerMainLogic(event.context)
        }
    }

    private fun onViewPaused(context: Context?) {

        if (context != null) {
            if (timerState == TimerState.Running) {
                timer.cancel()
                val currentNowSeconds = nowSeconds

                val wakeUpTime = alarmUtil.setAlarm(context, currentNowSeconds, secondsRemaining)

                PrefUtilImpl().apply {
                    setAlarmSetTime(currentNowSeconds, context)

                    if (isNotificationAllowed(context))
                        notificationUtil.showTimerRunning(context, wakeUpTime)
                }


            } else if (timerState == TimerState.Paused) {
                if (prefUtil.isNotificationAllowed(context))
                    notificationUtil.showTimerPaused(context)
            }


            prefUtil.apply {
                setPreviousTimerLengthSeconds(timerLengthSeconds, context)
                setSecondsRemaining(secondsRemaining, context)
                setTimerState(timerState, context)
            }

        }
    }

    private fun initTimerMainLogic(context: Context?) {
        initTimer(context)


        if (context != null) {
            // remove alarm
            alarmUtil.removeAlarm(context)
            prefUtil.setAlarmSetTime(0, context)
            notificationUtil.hideTimerNotification(context)
        }

    }

    private fun startTimerMainLogic(context: Context?) {
        if (context != null) {
            startTimer(context)
            timerState = TimerState.Running
            updateButtons()
        }
    }

    private fun cancelTimerMainLogic(context: Context?) {
        if (this::timer.isInitialized) {
            timer.cancel()
            context?.let { onTimerFinished(it) }
        }
    }

    private fun pauseTimerMainLogic() {
        timer.cancel()
        timerState = TimerState.Paused
        updateButtons()
    }

    /*
    * Business
    *
    * */
    private fun initTimer(context: Context?) {
        if (context != null) {
            timerState = prefUtil.getTimerState(context)


            if (timerState == TimerState.Stopped)
                setNewTimerLength(context)
            else
                setPreviousTimerLength(context)


            secondsRemaining =
                if (timerState == TimerState.Running || timerState == TimerState.Paused)
                    prefUtil.getSecondsRemaining(context)
                else
                    timerLengthSeconds


            val alarmSetTime = prefUtil.getAlarmSetTime(context)
            if (alarmSetTime > 0)
                secondsRemaining -= nowSeconds - alarmSetTime

            if (secondsRemaining <= 0)
                onTimerFinished(context)
            else if (timerState == TimerState.Running)
                startTimer(context)



            updateButtons()
            updateCountdownUI()
        }

    }

    private fun setNewTimerLength(context: Context) {
        val lengthInMinutes = prefUtil.getTimerLength(context)
        timerLengthSeconds = (lengthInMinutes * 60L)
        progressMaxValue.value = timerLengthSeconds.toInt()
    }

    private fun setPreviousTimerLength(context: Context) {
        timerLengthSeconds = prefUtil.getPreviousTimerLengthSeconds(context)
        progressMaxValue.value = timerLengthSeconds.toInt()
    }

    private fun onTimerFinished(context: Context) {
        timerState = TimerState.Stopped
        //set the length of the timer to be the one set in SettingsActivity
        //if the length was changed when the timer was running
        setNewTimerLength(context)
        progressValue.value = 0

        prefUtil.setSecondsRemaining(timerLengthSeconds, context)
        secondsRemaining = timerLengthSeconds

        updateButtons()
        updateCountdownUI()

    }

    private fun startTimer(context: Context) {
        timerState = TimerState.Running
        timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
            override fun onFinish() = onTimerFinished(context)

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                updateCountdownUI()
            }
        }.start()
    }


    private fun updateCountdownUI() {
        val minutesUntilFinished = secondsRemaining / 60
        val secondsInMinuteUntilFinished = secondsRemaining - minutesUntilFinished * 60
        val secondsStr = secondsInMinuteUntilFinished.toString()

        timerCountDownTextValue.value =
            "$minutesUntilFinished:${if (secondsStr.length == 2) secondsStr else "0$secondsStr"}"
        progressValue.value = (timerLengthSeconds - secondsRemaining).toInt()

    }

    private fun updateButtons() {
        when (timerState) {
            TimerState.Running -> {
                startButtonEnabledStatus.value = false
                pauseButtonEnabledStatus.value = true
                stopButtonEnabledStatus.value = true
            }
            TimerState.Stopped -> {
                startButtonEnabledStatus.value = true
                pauseButtonEnabledStatus.value = false
                stopButtonEnabledStatus.value = false
            }
            TimerState.Paused -> {
                startButtonEnabledStatus.value = true
                pauseButtonEnabledStatus.value = false
                stopButtonEnabledStatus.value = true
            }
        }
    }
}