package mo.egy.timermvvm.ui.timer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.timer_view.*
import mo.egy.timermvvm.R
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class TimerView : Fragment(),
    View.OnClickListener,
    KodeinAware {
    // inject
    override val kodein by closestKodein()

    // viewModel
    private lateinit var viewModel: TimerVIewModel
    private val viewModelFactory: TimerViewModelFactory by instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.timer_view, container, false)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel =
            ViewModelProviders.of(this, viewModelFactory)[TimerVIewModel::class.java]

        viewmodelObserver()

        fab_start.setOnClickListener(this)
        fab_pause.setOnClickListener(this)
        fab_stop.setOnClickListener(this)

    }

    private fun viewmodelObserver() {
        if (this::viewModel.isInitialized)
            with(viewModel) {

                error.observe(viewLifecycleOwner) { errorMessage ->
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }

                progressValue.observe(viewLifecycleOwner) {
                    if (it != null)
                        progress_countdown.progress = it
                }
                progressMaxValue.observe(viewLifecycleOwner) {
                    if (it != null)
                        progress_countdown.max = it
                }
                timerCountDownTextValue.observe(viewLifecycleOwner) {
                    if (it != null) textView_countdown.text = it
                }

                stopButtonEnabledStatus.observe(viewLifecycleOwner) {
                    fab_stop.isEnabled = it
                }
                startButtonEnabledStatus.observe(viewLifecycleOwner) {
                    fab_start.isEnabled = it
                }
                pauseButtonEnabledStatus.observe(viewLifecycleOwner) {
                    fab_pause.isEnabled = it
                }

            }
    }

    override fun onResume() {
        super.onResume()

        if (this::viewModel.isInitialized)
            viewModel.handleEvent(TimerViewEvent.OnViewResumed(context))


    }

    override fun onPause() {
        super.onPause()

        if (this::viewModel.isInitialized)
            viewModel.handleEvent(TimerViewEvent.OnViewPaused(context))

    }

    override fun onClick(view: View?) {
        if (view != null)
            if (this::viewModel.isInitialized)
                when (view.id) {
                    R.id.fab_start ->
                        viewModel.handleEvent(TimerViewEvent.StartTimer(context))
                    R.id.fab_pause ->
                        viewModel.handleEvent(TimerViewEvent.PausedTimer)
                    R.id.fab_stop ->
                        viewModel.handleEvent(TimerViewEvent.CancelTimer(context))
                }
    }

}