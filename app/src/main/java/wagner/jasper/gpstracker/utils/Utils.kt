package wagner.jasper.gpstracker.utils

import java.util.*


object Utils {

    fun round(value: Double, places: Int): Double {
        var value = value
        if (places < 0) throw IllegalArgumentException()

        val factor = Math.pow(10.0, places.toDouble()).toLong()
        value *= factor
        val tmp = Math.round(value)
        return tmp.toDouble() / factor
    }

    fun getDate(): String {
        val calender = Calendar.getInstance()

        val day = calender.get(Calendar.DAY_OF_MONTH)
        val month = calender.get(Calendar.MONTH)
        val year = calender.get(Calendar.YEAR)
        return "$day-$month-$year"
    }

    fun getTime(): String {
        val calender = Calendar.getInstance()

        val day = calender.get(Calendar.DAY_OF_MONTH)
        val month = calender.get(Calendar.MONTH)
        val year = calender.get(Calendar.YEAR)
        val hour = calender.get(Calendar.HOUR)
        val minute = calender.get(Calendar.MINUTE)
        return "$day-$month-$year $hour:$minute"
    }

    val VMG_FRAGMENT_TAG = 1
    val CCOMPASS_FRAGMENT_TAG = 2
    val SPEED_BEARING_FRAGMENT_TAG = 0

}