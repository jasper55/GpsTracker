package wagner.jasper.gpstracker.ui.main

import android.app.Application
import android.content.Intent
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import wagner.jasper.gpstracker.services.LocationProvider

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
    fun updateGPS(accuracy: String){
        _gpsAccuracy.value = accuracy
    }

    fun updateUI(speed: String?, heading: String?, altitude: String?, accuracy: String?) {
        _gpsAccuracy.value = accuracy
        _speed.value = speed
        _heading.value = heading
        _altitude.value = altitude
    }


}
