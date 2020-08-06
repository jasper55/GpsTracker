package wagner.jasper.gpstracker.services

import android.Manifest
import android.app.Service
import android.content.*
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import wagner.jasper.gpstracker.utils.Bearing
import wagner.jasper.gpstracker.utils.Utils


class LocationApi : Service() {

    private lateinit var localBroadcastManager: LocalBroadcastManager
    private lateinit var startTrackingReceiver: BroadcastReceiver
    private var currentTime: Long? = null
    private var previousTime: Long? = null
    private var firstLocation: Location? = null
    private var newLocation: Location? = null
    private var prevLocation: Location? = null
    private var locationProviderSource = "GPS"
    private var numberOfSatellites: Int? = null

    private val mBinder = ServiceBinder()

    override fun onBind(p0: Intent?): IBinder? {
        return mBinder
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    private lateinit var mLocationManager: LocationManager

    private var mLocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location?) {
            Log.d("LOCATION", " location received ")
            newLocationReceived(location)
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            Log.d("LOCATION", " Status Changed ")
            checkSatelittes()

        }

        override fun onProviderEnabled(provider: String?) {
            Log.d("LOCATION", " Provider Enabled")
        }

        override fun onProviderDisabled(provider: String?) {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    private fun checkSatelittes() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

    }


    override fun onCreate() {
        super.onCreate()
        val sharedPreference =  getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
        locationProviderSource =  sharedPreference.getString("PROVIDER","GPS")!!
        Log.d("Location",locationProviderSource)
        localBroadcastManager = LocalBroadcastManager.getInstance(applicationContext!!)
        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        initStartTrackingReceiver()
        requestLocationUpdates(locationProviderSource)
        Log.d("LOCATION", " service started ")
    }

    private fun requestLocationUpdates(provider: String) {


        val criteria = Criteria().apply {
            accuracy = Criteria.ACCURACY_FINE
            isAltitudeRequired = false
            isBearingRequired = true
            isCostAllowed = true
            powerRequirement = Criteria.NO_REQUIREMENT
        }

//        val provider = mLocationManager.getBestProvider(criteria, true)
        startLocationUpdates(provider)

        Log.d("LOCATION", "best Provider: $provider")
    }

    private fun initStartTrackingReceiver() {
        startTrackingReceiver = object : BroadcastReceiver() {
            override fun onReceive(contxt: Context?, intent: Intent?) {
                firstLocation = newLocation
            }
        }
        val filter = IntentFilter(LocationProvider.BR_FIRST_LOCATION)
        applicationContext.registerReceiver(startTrackingReceiver, filter)
    }

    private fun startLocationUpdates(providerChosen: String) {
        var provider = ""
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        provider = if (providerChosen == "GPS"){
            LocationManager.GPS_PROVIDER
        } else {
            LocationManager.NETWORK_PROVIDER
        }
        Log.d("Provider",provider)


        mLocationManager.requestLocationUpdates(provider, 100, 0f, mLocationListener)

    }


    inner class ServiceBinder : Binder() {
        val service: LocationApi
            get() = this@LocationApi
    }


    private fun sendBroadcastIntent() {
        val intent = Intent(LocationProvider.BR_NEW_LOCATION)
        //intent.putExtra(LocationProvider.KEY_DISTANCE, getDistanceInMeters)
        //intent.putExtra(LocationProvider.KEY_TIME, timeElapsed)
        intent.putExtra(LocationProvider.KEY_HEADING, getHeading)
        intent.putExtra(LocationProvider.KEY_HEADING_CALC, getHeadingCalc)
        intent.putExtra(LocationProvider.KEY_SPEED, getSpeed)
        intent.putExtra(LocationProvider.KEY_ACCURACY, getGPSAccuracy)
        intent.putExtra(LocationProvider.KEY_ALTITUDE, getAltitude)
        intent.putExtra(LocationProvider.KEY_LOCATION, newLocation)
        intent.putExtra(LocationProvider.KEY_DISTANCE, distanceSequment)
        intent.putExtra(LocationProvider.KEY_PROVIDER_SOURCE, providerSource)
        intent.putExtra(LocationProvider.KEY_SATELLITES, numberOfSatellites.toString())
        //intent.putExtra(LocationProvider.KEY_CURRENT_TIME, currentTime)
        localBroadcastManager.sendBroadcast(intent)
        Log.i("Location Debugging", "sendBroadcast invoked")
    }


    private fun getSatellitesAmount() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        var i = 0
        mLocationManager.getGpsStatus(null).satellites.forEach { satellite ->
            Log.d("Location Satellite: ",satellite.toString())
            i += 1
        }
        numberOfSatellites = i
    }
    private fun newLocationReceived(lastLocation: Location?) {
        if(lastLocation!!.isFromMockProvider) return

        if (!lastLocation.hasSpeed()) return

        if (newLocation == null){
            newLocation = lastLocation
            firstLocation = lastLocation

            Log.i("Location Debugging", "first Location sent")
        } else {
            prevLocation = newLocation
            newLocation = lastLocation
            if (prevLocation?.latitude != newLocation?.latitude && prevLocation?.longitude != newLocation?.longitude) {
                previousTime = currentTime
                currentTime = System.currentTimeMillis()
                sendBroadcastIntent()
                Log.i("Location Debugging", "Location sent")
            }
            else {
                Log.i("Location Debugging", "Location didn't change")
            }
        }
    }

    private val getGPSAccuracy: String
        get() {
            var acc = LocationProvider.VALUE_MISSING
            newLocation?.let {
                if (it.hasAccuracy()) {
                    acc = Utils.round(it.accuracy.toDouble(), 1).toString()
                }
            }
            return acc
        }

    private val getSpeed: String
        get() {
            var speedString = LocationProvider.VALUE_MISSING
            prevLocation?.let {
                val meters = prevLocation?.distanceTo(newLocation)
                if (previousTime != null) {
                    val millis = currentTime!!.minus(previousTime!!)

                    val speed = meters!! / (millis / 1000f)
                    if (speed == Float.POSITIVE_INFINITY) {
                        Log.i("speed: ", "$speed")
                        Log.i("distance: ", "$millis")
                        Log.i("meters: ", "$meters")
                        return LocationProvider.VALUE_MISSING
                    }
                    speedString = "${Utils.round(speed.toDouble(), 1)} m/s"
                }
            }
            return speedString
        }

    val getHeading: String
        get() {
            var bearing = LocationProvider.VALUE_MISSING
            prevLocation?.let {
                var bear = Utils.round((prevLocation!!.bearingTo(newLocation)).toDouble(), 0)
                if (bear < 0) {
                    bear = 270 - (bear + 90)
                }
                bearing = "${bear.toInt()} °"
            }
            return bearing
        }

    val getHeadingCalc: String
        get() {
            var bearing = LocationProvider.VALUE_MISSING
            prevLocation?.let {
                var bear = Bearing.calculateBetween(prevLocation!!, newLocation!!)
/*                if (bear < 0) {
                    bear = 270 - (bear + 90)
                }*/
                bearing = "${bear.toInt()} °"
            }
            return bearing
        }

    val getAltitude: String
        get() {
            var altitude = LocationProvider.VALUE_MISSING
            newLocation?.let {
                if (it.hasAltitude())
                    altitude = "${Utils.round(it.altitude, 1)} m"
            }
            return altitude
        }

    val providerSource: String
        get() {
            var provider = LocationProvider.VALUE_MISSING
            newLocation?.let {

                provider = it.provider.toString()
            }
            return provider
        }

    val distanceSequment: String
        get() {
            Log.d("NewLocation", newLocation.toString())
            Log.d("FirstLocation", firstLocation.toString())
            var distance = LocationProvider.VALUE_MISSING
            newLocation?.let { new ->
                firstLocation?.let { first ->
                    distance = "${Utils.round(new.distanceTo(first).toDouble(), 2)} m"
                }
            }
            return distance
        }

}