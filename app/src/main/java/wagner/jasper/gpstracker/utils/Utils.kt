package wagner.jasper.gpstracker.utils


object Utils {

    fun round(value: Double, places: Int): Double {
        var value = value
        if (places < 0) throw IllegalArgumentException()

        val factor = Math.pow(10.0, places.toDouble()).toLong()
        value *= factor
        val tmp = Math.round(value)
        return tmp.toDouble() / factor
    }

    val VMG_FRAGMENT_TAG = 1
    val CCOMPASS_FRAGMENT_TAG = 2
    val SPEED_BEARING_FRAGMENT_TAG = 0

}