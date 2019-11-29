package wagner.jasper.gpstracker.gpxparser

import java.util.ArrayList
import java.util.Collections

class Track private constructor(builder: Builder) {
    val trackName: String?
    val trackSegments: List<TrackSegment>
    val trackDesc: String?
    val trackCmt: String?
    val trackSrc: String?
    val trackNumber: Int?
    val trackLink: Link?
    val trackType: String?

    init {
        trackName = builder.mTrackName
        trackDesc = builder.mTrackDesc
        trackCmt = builder.mTrackCmt
        trackSrc = builder.mTrackSrc
        trackNumber = builder.mTrackNumber
        trackSegments =
            Collections.unmodifiableList<TrackSegment>(ArrayList<TrackSegment>(builder.mTrackSegments!!))
        trackLink = builder.mTrackLink
        trackType = builder.mTrackType
    }

    class Builder {
        internal var mTrackName: String? = null
        internal var mTrackSegments: List<TrackSegment>? = null
        internal var mTrackDesc: String? = null
        internal var mTrackCmt: String? = null
        internal var mTrackSrc: String? = null
        internal var mTrackNumber: Int? = null
        internal var mTrackLink: Link? = null
        internal var mTrackType: String? = null

        fun setTrackName(trackName: String): Builder {
            mTrackName = trackName
            return this
        }

        fun setTrackDesc(trackDesc: String): Builder {
            mTrackDesc = trackDesc
            return this
        }

        fun setTrackSegments(trackSegments: List<TrackSegment>): Builder {
            mTrackSegments = trackSegments
            return this
        }

        fun setTrackCmt(trackCmt: String): Builder {
            mTrackCmt = trackCmt
            return this
        }

        fun setTrackSrc(trackSrc: String): Builder {
            mTrackSrc = trackSrc
            return this
        }

        fun setTrackNumber(trackNumber: Int?): Builder {
            mTrackNumber = trackNumber
            return this
        }

        fun setTrackLink(link: Link): Builder {
            mTrackLink = link
            return this
        }

        fun setTrackType(type: String): Builder {
            mTrackType = type
            return this
        }

        fun build(): Track {
            return Track(this)
        }


    }
}