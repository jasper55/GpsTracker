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
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import wagner.jasper.gpstracker.extensions.show
import wagner.jasper.gpstracker.R
import wagner.jasper.gpstracker.services.LocationProvider
import java.util.*
import kotlin.collections.ArrayList

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
    private var fileName: String = ""
    private var fileNameNumber = 0
    private var locationList = ArrayList<Location>()

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

    override fun onStop(){
        super.onStop()
        context!!.unregisterReceiver(locationBroadcastReceiver)
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
                viewModel.enableGPS(activity!!)
                customToast.show(context, "GPS Provider enabled", Gravity.BOTTOM, Toast.LENGTH_SHORT)
            } else {
                viewModel.disableGPS(activity!!)
                customToast.show(context, "GPS Provider disabled", Gravity.BOTTOM, Toast.LENGTH_SHORT)
            }
        }

        switchTracking!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                locationList = ArrayList<Location>()
                //viewModel.startTracking()
                fileName="${getDate()}_$fileNameNumber"
                customToast.show(context, "Tracking started", Gravity.BOTTOM, Toast.LENGTH_SHORT)
            } else {
                viewModel.saveLocationList(locationList)
                viewModel.saveRoute(activity!!,fileName,getTime())
                fileNameNumber=+1
                customToast.show(context, "Tracking stopped", Gravity.BOTTOM, Toast.LENGTH_SHORT)
            }
        }
    }

    private fun getDate(): String {
        val calender = Calendar.getInstance()

        val day = calender.get(Calendar.DAY_OF_MONTH)
        val month = calender.get(Calendar.MONTH)
        val year = calender.get(Calendar.YEAR)
        return "$day-$month-$year"
    }

    private fun getTime(): String {
        val calender = Calendar.getInstance()

        val day = calender.get(Calendar.DAY_OF_MONTH)
        val month = calender.get(Calendar.MONTH)
        val year = calender.get(Calendar.YEAR)
        val hour = calender.get(Calendar.HOUR)
        val minute = calender.get(Calendar.MINUTE)
        return "$day-$month-$year $hour:$minute"
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
                val location = intent.getParcelableExtra<Location>(LocationProvider.KEY_LOCATION)

                viewModel.updateUI(speed,heading,altitude,accuracy)
                //viewModel.addToList(location)
                locationList.add(location)
            }
        }
        val filter = IntentFilter(LocationProvider.BR_NEW_LOCATION)
        context!!.registerReceiver(locationBroadcastReceiver,filter)
    }

}
