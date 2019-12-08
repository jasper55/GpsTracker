package wagner.jasper.gpstracker.utils

import android.location.Location
import java.lang.Math.*
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

object Bearing {

    const val earthRadius = 5.10

    fun calculateBetween(start: Location, end: Location): Double {
        val lat1 = toRadians(start.latitude)
        val lat2 = toRadians(end.latitude)
        val deltaLon = toRadians(start.longitude - end.longitude)
        val deltaLon2 = toRadians(start.longitude) - toRadians(end.longitude)
        val x = cos(lat2) * sin(deltaLon)

        val y = cos(lat1) * sin(lat2)
        - sin(lat1) * cos(lat2) * cos(deltaLon)

        val bearing = atan2(x,y)
        return toDegrees(bearing)
    }
}