package wagner.jasper.gpstracker.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.android.synthetic.main.main_fragment.*
import wagner.jasper.gpstracker.extensions.show
import wagner.jasper.gpstracker.R
import wagner.jasper.gpstracker.services.LocationProvider

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var locationBroadcastReceiver: BroadcastReceiver
    private lateinit var viewModel: MainViewModel
    private var switchProvider: Switch? = null
    private var switchTracking: Switch? = null
    private var tvAccuracy: TextView? = null
    private var tvSpeed: TextView? = null
    private var tvHeading: TextView? = null
    private var tvAltitude: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.main_fragment, container, false)
        initializeView(view)
        return view }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        initBroadcastReceiver()
        observeLiveDataChanges()
    }

    private fun observeLiveDataChanges() {
        viewModel.gpsAccuracy.observe(this.viewLifecycleOwner, Observer {
            tvAccuracy!!.text = it.toString()
        })
                viewModel.speed.observe(this.viewLifecycleOwner, Observer {
                    tvSpeed!!.text = it.toString()
                })
        viewModel.heading.observe(this.viewLifecycleOwner, Observer {
            tvHeading!!.text = it.toString()
        })
        viewModel.altitude.observe(this.viewLifecycleOwner, Observer {
            tvAltitude!!.text = it.toString()
        })
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(activity!!.applicationContext)
            .registerReceiver(locationBroadcastReceiver, IntentFilter(LocationProvider.BR_NEW_LOCATION))
        observeLiveDataChanges()
    }

    private fun initializeView(view: View) {
        tvAccuracy = view.findViewById(R.id.tvAccuracy)
        tvSpeed = view.findViewById(R.id.tvSpeed)
        tvHeading = view.findViewById(R.id.tvHeading)
        tvAltitude = view.findViewById(R.id.tvAltitude)

        switchTracking = view.findViewById(R.id.switchTracking)
        switchProvider = view.findViewById(R.id.switchProvider)
        val customToast = Toast(context)
        switchProvider!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                viewModel.enableGPS()
                customToast.show(context, "GPS Provider enabled", Gravity.BOTTOM, Toast.LENGTH_SHORT)
            } else {
                viewModel.disableGPS()
                customToast.show(context, "GPS Provider disabled", Gravity.BOTTOM, Toast.LENGTH_SHORT)
            }
        }
    }

    private fun initBroadcastReceiver() {
        locationBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(contxt: Context?, intent: Intent?) {
                val meters = intent!!.getDoubleExtra(LocationProvider.KEY_DISTANCE, 0.0)
                val heading = intent.getStringExtra(LocationProvider.KEY_HEADING)
                val speed = intent.getStringExtra(LocationProvider.KEY_SPEED)
                val accuracy = intent.getStringExtra(LocationProvider.KEY_ACCURACY)
                val elapsedTime = intent.getDoubleExtra(LocationProvider.KEY_TIME, 0.0)
                val altitude = intent.getStringExtra(LocationProvider.KEY_ALTITUDE)


                viewModel.updateUI(speed,heading,altitude,accuracy)
            }
        }
    }

}
