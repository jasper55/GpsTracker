package wagner.jasper.gpstracker.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
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
import wagner.jasper.gpstracker.extensions.show
import wagner.jasper.gpstracker.R
import wagner.jasper.gpstracker.services.LocationProvider
import wagner.jasper.gpstracker.services.LocationProvider.Companion.BR_FIRST_LOCATION
import wagner.jasper.gpstracker.services.LocationProvider.Companion.VALUE_MISSING
import wagner.jasper.gpstracker.utils.Utils.getDate
import wagner.jasper.gpstracker.utils.Utils.getTime
import wagner.jasper.gpstracker.utils.Utils.round

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var localBroadcastManager: LocalBroadcastManager
    private lateinit var locationBroadcastReceiver: BroadcastReceiver
    private lateinit var viewModel: MainViewModel
    private var switchProvider: Switch? = null
    private var switchTracking: Switch? = null
    private var tvProviderSource: TextView? = null
    private var tvAccuracy: TextView? = null
    private var tvSpeed: TextView? = null
    private var tvHeading: TextView? = null
    private var tvHeadingCalc: TextView? = null
    private var tvAltitude: TextView? = null
    private var tvElapsedTimeCurrentRun: TextView? = null
    private var tvDistanceCurrentRun: TextView? = null
    private var fileName: String = ""
    private var fileNameNumber = 0
    private var trackingIsRunning = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.main_fragment, container, false)
        initializeView(view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        observeLiveDataChanges()
        localBroadcastManager =LocalBroadcastManager.getInstance(activity!!.applicationContext!!)
    }

    private fun observeLiveDataChanges() {
        viewModel.providerSource.observe(this.viewLifecycleOwner, Observer {
            tvProviderSource!!.text = it.toString()
        })
        viewModel.gpsAccuracy.observe(this.viewLifecycleOwner, Observer {
            tvAccuracy!!.text = it.toString()
        })
        viewModel.speed.observe(this.viewLifecycleOwner, Observer {
            tvSpeed!!.text = it.toString()
        })
        viewModel.heading.observe(this.viewLifecycleOwner, Observer {
            tvHeading!!.text = it.toString()
        })
        viewModel.headingCalc.observe(this.viewLifecycleOwner, Observer {
            tvHeadingCalc!!.text = it.toString()
        })
        viewModel.altitude.observe(this.viewLifecycleOwner, Observer {
            tvAltitude!!.text = it.toString()
        })
        viewModel.distanceCurrentRun.observe(this.viewLifecycleOwner, Observer {
            tvDistanceCurrentRun!!.text = it.toString()
        })
        viewModel.elapsedTimeCurrentRun.observe(this.viewLifecycleOwner, Observer {
            tvElapsedTimeCurrentRun!!.text = it.toString()
        })
    }

    override fun onResume() {
        super.onResume()
        observeLiveDataChanges()
    }

    override fun onStart() {
        super.onStart()
        initBroadcastReceiver()
        localBroadcastManager
            .registerReceiver(locationBroadcastReceiver,
                IntentFilter(LocationProvider.BR_NEW_LOCATION))
    }

    override fun onDestroy() {
        super.onDestroy()
        context!!.unregisterReceiver(locationBroadcastReceiver)
    }

    private fun initializeView(view: View) {
        tvProviderSource = view.findViewById(R.id.tvProviderSource)
        tvAccuracy = view.findViewById(R.id.tvAccuracy)
        tvSpeed = view.findViewById(R.id.tvSpeed)
        tvHeading = view.findViewById(R.id.tvHeading)
        tvHeadingCalc = view.findViewById(R.id.tvHeading_calc)
        tvAltitude = view.findViewById(R.id.tvAltitude)
        tvElapsedTimeCurrentRun = view.findViewById(R.id.tvElapsedTimeCurrentRun)
        tvDistanceCurrentRun = view.findViewById(R.id.tvDistanceCurrentRun)

        switchTracking = view.findViewById(R.id.switchTracking)
        switchProvider = view.findViewById(R.id.switchProvider)
        val customToast = Toast(context)
        switchProvider!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                viewModel.enableGPS(activity!!)
                customToast.show(
                    context,
                    "GPS Provider enabled",
                    Gravity.BOTTOM,
                    Toast.LENGTH_SHORT
                )
            } else {
                viewModel.disableGPS(activity!!)
                context!!.unregisterReceiver(locationBroadcastReceiver)
                customToast.show(
                    context,
                    "GPS Provider disabled",
                    Gravity.BOTTOM,
                    Toast.LENGTH_SHORT
                )
            }
        }

        switchTracking!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                trackingIsRunning = true
                //sendStartTrackingIntent()
                viewModel.setStartTime()
                viewModel.startTracking()
                sendStartTrackingIntent()
                fileName = "${getDate()}_$fileNameNumber"
                customToast.show(context, "Tracking started", Gravity.BOTTOM, Toast.LENGTH_SHORT)
            } else {
                trackingIsRunning = false
                viewModel.saveTracking(activity!!, fileName, getTime())
                viewModel.update(VALUE_MISSING, false)
                fileNameNumber = +1
                customToast.show(context, "Tracking stopped", Gravity.BOTTOM, Toast.LENGTH_SHORT)
            }
        }
    }

    private fun sendStartTrackingIntent() {
        val intent = Intent(BR_FIRST_LOCATION)
        localBroadcastManager.sendBroadcast(intent)
    }

    private fun initBroadcastReceiver() {
        locationBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(contxt: Context?, intent: Intent?) {

                intent?.apply {
                    Log.i("Location Debugging",action.toString())
                    val heading = getStringExtra(LocationProvider.KEY_HEADING)
                    val headingCalc = getStringExtra(LocationProvider.KEY_HEADING_CALC)
                    val speed = getStringExtra(LocationProvider.KEY_SPEED)
                    val accuracy = getStringExtra(LocationProvider.KEY_ACCURACY)
                    val providerSource = getStringExtra(LocationProvider.KEY_PROVIDER_SOURCE)
                    val distanceCurrentRun = getStringExtra(LocationProvider.KEY_DISTANCE)
                    val altitude = getStringExtra(LocationProvider.KEY_ALTITUDE)
                    val location = getParcelableExtra<Location>(LocationProvider.KEY_LOCATION)

                    viewModel.updateUI(
                        speed,
                        heading,
                        headingCalc,
                        altitude,
                        accuracy,
                        providerSource
                    )
                    if (trackingIsRunning) {
                        viewModel.update(distanceCurrentRun, true)
                        viewModel.addToList(location)
                    }
                    //locationList.add(location)
                }
            }
        }
    }

}
