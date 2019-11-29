package wagner.jasper.gpstracker.ui.main

import android.app.Application
import android.content.Intent
import android.location.Location
import android.os.Environment
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import wagner.jasper.gpstracker.services.LocationProvider
import wagner.jasper.gpstracker.utils.SaveGpx
import java.io.File


class MainViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext
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

    private val _altitude = MutableLiveData<String>()
    val altitude: LiveData<String>
        get() = _altitude

    fun enableGPS() {
        context.startService(Intent(context, LocationProvider::class.java))
    }

    fun disableGPS() {
        context.stopService(Intent(context, LocationProvider::class.java))
    }

    fun updateUI(speed: String?, heading: String?, altitude: String?, accuracy: String?) {
        _gpsAccuracy.value = accuracy
        _speed.value = speed
        _heading.value = heading
        _altitude.value = altitude
    }

    fun addToList(location: Location){
        _locationList.value!!.add(location)
    }


    fun startTracking() {
        _locationList.value = ArrayList()
    }

    fun saveRoute(filename: String, time: String) {

// routeFile = new File(getFilesDir(), FILENAME);


        val root = Environment.getExternalStorageState().toString()
        val myDir = File("$root/gps")
        myDir.mkdirs()

        val file = File(myDir, "$filename.gpx")
        val name = "Jasper Wagner"

        val gpxFile = SaveGpx(context)
        try {
            file.createNewFile()
            gpxFile.writePath(file, name, time, locationList.value!!)

            //   Log.i(TAG, "Route Saved " + file.getName());

        } catch (e: Exception) {
            Log.e("WritingFile", "Not completed writing" + file.name)
        }

    }



}
