package wagner.jasper.gpstracker.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
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
import androidx.appcompat.app.AlertDialog
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
    private var fileNameNumber: Int = 0
    private var startTime: Long? = null
    private var trackingIsRunning = false
    private var gpsIsEnabled = false
    private lateinit var customToast: Toast

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
        fileNameNumber = viewModel.getLastFileNumber(context!!.filesDir)
        initBroadcastReceiver()

        LocalBroadcastManager.getInstance(activity!!.applicationContext)
            .registerReceiver(locationBroadcastReceiver, IntentFilter(LocationProvider.BR_NEW_LOCATION))
        observeLiveDataChanges()

        customToast = Toast(context)
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
//        LocalBroadcastManager.getInstance(activity!!.applicationContext)
//            .registerReceiver(
//                locationBroadcastReceiver,
//                IntentFilter(LocationProvider.BR_NEW_LOCATION)
//            )
        observeLiveDataChanges()
    }

    override fun onDestroy() {
        super.onDestroy()
        //context!!.unregisterReceiver(locationBroadcastReceiver)
        LocalBroadcastManager.getInstance(activity!!.applicationContext)
            .unregisterReceiver(locationBroadcastReceiver)
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
        switchProvider!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                viewModel.enableGPS(activity!!)
                gpsIsEnabled = true
                customToast.show(
                    context,
                    "GPS Provider enabled",
                    Gravity.BOTTOM,
                    Toast.LENGTH_SHORT
                )
            } else {
                viewModel.disableGPS(activity!!)
                gpsIsEnabled = false
                try {
                    context!!.unregisterReceiver(locationBroadcastReceiver)
                } catch (e: Exception) {
                }
                customToast.show(
                    context,
                    "GPS Provider disabled",
                    Gravity.BOTTOM,
                    Toast.LENGTH_SHORT
                )
            }
        }

        switchTracking!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked && gpsIsEnabled) {
                trackingIsRunning = true
                fileNameNumber += 1
                sendStartTrackingIntent()
                setStartTime()
                viewModel.startTracking()
                fileName = "${getDate()}_$fileNameNumber"
                customToast.show(context, "Tracking started", Gravity.BOTTOM, Toast.LENGTH_SHORT)
            } else if (!isChecked && gpsIsEnabled) {
                trackingIsRunning = false
                showSaveDialog()
                viewModel.update(VALUE_MISSING, VALUE_MISSING)
            } else if (isChecked && !gpsIsEnabled) {
                customToast.show(
                    context,
                    "gps not enabled",
                    Gravity.CENTER_VERTICAL,
                    Toast.LENGTH_LONG,
                    isErrorToast = true
                )
            } else if (!isChecked && !gpsIsEnabled) {
                customToast.show(
                    context,
                    "gps not enabled",
                    Gravity.CENTER_VERTICAL,
                    Toast.LENGTH_LONG,
                    isErrorToast = true
                )
            }
        }
    }

    private fun showSaveDialog() {
        val builder = AlertDialog.Builder(context!!,androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert)
        builder.setTitle("Save Tracking")
        builder.setMessage("Are you sure, you want to save the tracked route?")

        builder.setPositiveButton("YES") { _, _ ->
            viewModel.saveTracking(activity!!, fileName, getTime())
            customToast.show(context, "Tracking saved", Gravity.BOTTOM, Toast.LENGTH_SHORT)
        }

        builder.setNegativeButton("NO") { dialog, which ->
            fileNameNumber -= 1
            customToast.show(context, "Tracking discarded", Gravity.BOTTOM, Toast.LENGTH_SHORT)
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun setStartTime() {
        startTime = System.currentTimeMillis()
    }

    private val getTimeElapsed: String
        get() {
            var time = VALUE_MISSING
            val currentTime = System.currentTimeMillis()
            startTime?.let {
                val diff = round(((currentTime - it) / 1000.0), 1)
                time = "$diff s"
            }
            return time
        }

    private fun sendStartTrackingIntent() {
        val intent = Intent(BR_FIRST_LOCATION)
        LocalBroadcastManager.getInstance(this.context!!).sendBroadcast(intent)
    }

    private fun initBroadcastReceiver() {
        locationBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(contxt: Context?, intent: Intent?) {
                intent?.let {
                    val heading = it.getStringExtra(LocationProvider.KEY_HEADING)
                    val headingCalc = it.getStringExtra(LocationProvider.KEY_HEADING_CALC)
                    val speed = it.getStringExtra(LocationProvider.KEY_SPEED)
                    val accuracy = it.getStringExtra(LocationProvider.KEY_ACCURACY)
                    val providerSource = it.getStringExtra(LocationProvider.KEY_PROVIDER_SOURCE)
                    val distanceCurrentRun = it.getStringExtra(LocationProvider.KEY_DISTANCE)
                    val altitude = intent.getStringExtra(LocationProvider.KEY_ALTITUDE)
                    val location =
                        intent.getParcelableExtra<Location>(LocationProvider.KEY_LOCATION)

                    viewModel.updateUI(
                        speed,
                        heading,
                        headingCalc,
                        altitude,
                        accuracy,
                        providerSource
                    )
                    if (trackingIsRunning) {
                        viewModel.update(distanceCurrentRun, getTimeElapsed)
                        viewModel.addToList(location)
                    }
                    //locationList.add(location)
                }
            }
        }
//        val filter = IntentFilter(LocationProvider.BR_NEW_LOCATION)
//        context!!.registerReceiver(locationBroadcastReceiver, filter)
    }

}
