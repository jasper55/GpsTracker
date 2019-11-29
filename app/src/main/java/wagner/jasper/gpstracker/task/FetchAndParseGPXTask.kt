package wagner.jasper.gpstracker.task

import wagner.jasper.gpstracker.gpxparser.Gpx
import org.xmlpull.v1.XmlPullParserException
import wagner.jasper.gpstracker.gpxparser.GPXParser
import android.os.AsyncTask
import java.io.BufferedInputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class FetchAndParseGPXTask(
    internal val mGpxUrl: String,
    internal val mListener: GpxFetchedAndParsed
) : AsyncTask<Void, Void, Gpx>() {
    internal val mParser = GPXParser()

    override fun doInBackground(vararg unused: Void): Gpx? {
        var parsedGpx: Gpx? = null
        try {
            val url = URL(mGpxUrl)
            val client = url.openConnection() as HttpURLConnection
            val `in` = BufferedInputStream(client.inputStream)
            parsedGpx = mParser.parse(`in`)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        }

        return parsedGpx
    }

    override fun onPostExecute(gpx: Gpx) {
        mListener.onGpxFetchedAndParsed(gpx)
    }
}