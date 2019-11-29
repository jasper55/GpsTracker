package wagner.jasper.gpstracker.utils


import android.content.Context
import android.location.Location
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import wagner.jasper.gpstracker.extensions.show
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SaveGpx(val application: Context) {


    /**
     * Writes locations to gpx file format
     *
     * @param file file for the gpx
     * @param n name for the file
     * @param points List of locations to be written to gpx format
     */
    fun writePath(file: File, name: String, time: String, points: ArrayList<Location>) {
        val header =
            "<gpx creator=\"GPS Tracker\" version=\"1.1\" xmlns=\"http://www.topografix.com/GPX/1/1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">\n"
        val metadata =
            " <metadata>\n" + "   <time>$time</time>" + "\n  </metadata>"
        val name = " <trk>\n  <name>$name</name>\n  <trkseg>\n"

        var segments = ""
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")


        val stockList = ArrayList<String>()


        for (i in 0 until points.size) {

            stockList.add(
                "   <trkpt lat=\"" + points[i].latitude + "\" lon=\"" + points[i].longitude + "\">\n" +
                        "    <acc>" + points[i].accuracy + "</acc>\n" +
                        "    <bear>" + points[i].bearing + "</bear>\n" +
                        "    <bear>" + points[i].speed + "</bear>\n" +
                        "    <timeElapsed>" + points[i].elapsedRealtimeNanos + "</timeElapsed>\n" +
                        "    <provider>" + points[i].provider + "</provider>\n" +
                        "    <distance>" + points[i].distanceTo(points[i - 1]) + "</distance>\n" +
                        "    <ele>" + points[i].altitude + "</ele>\n" +
                        "    <time>" + df.format(Date()) + "Z</time>\n   </trkpt>\n"
            )
        }

        segments += stockList
        segments = segments.replace(",", "")
        segments = segments.replace("[", "")
        segments = segments.replace("]", "")

        val footer = "  </trkseg>\n </trk>\n</gpx>"

        try {
            val writer = FileWriter(file)

            writer.append(header)
            writer.append(metadata)
            writer.append(name)
            writer.append(segments)
            writer.append(footer)
            writer.flush()
            writer.close()

            Log.i(TAG, "Saved " + points.size + " points.")

        } catch (e: IOException) {
            Log.e(TAG, "Error Writting Path", e)
            val customToast = Toast(application)
            customToast.show(application, "Error while saving data: $e", Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK, Toast.LENGTH_SHORT)
        }
    }


    companion object {
        private val TAG = SaveGpx::class.java.name
    }


}