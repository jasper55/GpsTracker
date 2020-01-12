package wagner.jasper.gpstracker.ui.main

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import wagner.jasper.gpstracker.services.LocationProvider
import wagner.jasper.gpstracker.utils.GpxFile
import java.io.File
import wagner.jasper.gpstracker.R
import wagner.jasper.gpstracker.utils.Utils.getDate


class MainViewModel(
    application: Application
) : AndroidViewModel(application) {

    //private val context = getApplication<Application>().baseContext
    private val _gpsAccuracy = MutableLiveData<String>()
    val gpsAccuracy: LiveData<String>
        get() = _gpsAccuracy

    private val _speed = MutableLiveData<String>()
    val speed: LiveData<String>
        get() = _speed

    private val _locationList = MutableLiveData<ArrayList<Location>>()
    val locationList: LiveData<ArrayList<Location>>
        get() = _locationList

    private val _heading = MutableLiveData<String>()
    val heading: LiveData<String>
        get() = _heading

    private val _headingCalc = MutableLiveData<String>()
    val headingCalc: LiveData<String>
        get() = _headingCalc

    private val _altitude = MutableLiveData<String>()
    val altitude: LiveData<String>
        get() = _altitude

    private val _elapsedTimeCurrentRun = MutableLiveData<String>()
    val elapsedTimeCurrentRun: LiveData<String>
        get() = _elapsedTimeCurrentRun

    private val _distanceCurrentRun = MutableLiveData<String>()
    val distanceCurrentRun: LiveData<String>
        get() = _distanceCurrentRun

    private val _providerSource = MutableLiveData<String>()
    val providerSource: LiveData<String>
        get() = _providerSource


    fun enableGPS(context: Activity) {
        context.startService(Intent(context, LocationProvider::class.java))
    }

    fun disableGPS(context: Activity) {
        context.stopService(Intent(context, LocationProvider::class.java))
    }

    fun updateUI(speed: String?, heading: String?, headingCalc: String?, altitude: String?,
                 accuracy: String?, providerSource: String?) {

        _gpsAccuracy.value = accuracy
        _speed.value = speed
        _heading.value = heading
        _headingCalc.value = headingCalc
        _altitude.value = altitude
        _providerSource.value = providerSource
    }

    fun getLastFileNumber(directory: File): Int {
        val files = directory.listFiles()
        var lastFileNameNumber = 0
        if (files != null) {
            Log.i(TAG,"$files")
            for (file in files) {
                Log.i(TAG,"$file")
                if (file != null) {
                    val filename = file.name
                    if (filename.startsWith("${getDate()}")){
                    var number = filename.substringAfter("${getDate()}_")
                        .substringBefore(".gpx")
                    //lastFileNameNumber = number.toInt()
                    Log.i(TAG,"$filename")
                    Log.i(TAG,"$number")
                    lastFileNameNumber = number.toInt()
                    }
                }
            }
        }
        return lastFileNameNumber
    }

    fun addToList(location: Location){
        if(_locationList.value!!.isNullOrEmpty()){
            _locationList.value = ArrayList()
        }
        _locationList.value!!.add(location)
    }

    fun startTracking() {
        _locationList.value = ArrayList()
    }

    fun saveTracking(context: Activity, filename: String, time: String) {

        Log.i(TAG, "trying to save file $filename.gpx ...")

        val file = File(context.filesDir, "$filename.gpx")
        if (!file.exists()) {
            val directory = context.filesDir
            directory.mkdir()
        }
        val author = context.getString(R.string.AUTHOR)

        val gpxFile = GpxFile(context)
        try {
            gpxFile.createFile(file, author, time, locationList.value!!)
            Log.i(TAG, "File ${file.name} successfully saved")
        } catch (e: Exception) {
            Log.e(TAG, "Not completed saving file: " + file.name + " " + e)
        }
    }

    fun update(distanceCurrentRun: String?, timeElapsed: String) {
        _distanceCurrentRun.value = distanceCurrentRun
        _elapsedTimeCurrentRun.value = timeElapsed
    }

    companion object {
        private val TAG = MainViewModel::class.java.name
    }

}
