package wagner.jasper.gpstracker.gpxparser


import java.util.Collections.unmodifiableList
import kotlin.collections.ArrayList


class TrackSegment private constructor(builder: Builder) {
    val trackPoints: List<TrackPoint>

    init {
        trackPoints = unmodifiableList(ArrayList(builder.mTrackPoints!!))
    }

    class Builder {
        internal var mTrackPoints: List<TrackPoint>? = null

        fun setTrackPoints(trackPoints: ArrayList<TrackPoint>): Builder {
            mTrackPoints = trackPoints
            return this
        }

        fun build(): TrackSegment {
            return TrackSegment(this)
        }
    }
}