package wagner.jasper.gpstracker.task

import wagner.jasper.gpstracker.gpxparser.Gpx


interface GpxFetchedAndParsed {
    fun onGpxFetchedAndParsed(gpx: Gpx)
}