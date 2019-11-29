package wagner.jasper.gpstracker.gpxparser

import java.util.Collections.unmodifiableList
import kotlin.collections.ArrayList


class Gpx private constructor(builder: Builder) {
    val version: String?
    val creator: String?
    val metadata: Metadata?
    val wayPoints: List<WayPoint>
    val routes: List<Route>
    val tracks: List<Track>

    init {
        version = builder.mVersion
        creator = builder.mCreator
        metadata = builder.mMetadata
        wayPoints = unmodifiableList(ArrayList(builder.mWayPoints!!))
        routes = unmodifiableList(ArrayList(builder.mRoutes!!))
        tracks = unmodifiableList(ArrayList(builder.mTracks!!))
    }

    class Builder {
        internal var mWayPoints: List<WayPoint>? = null
        internal var mRoutes: List<Route>? = null
        internal var mTracks: List<Track>? = null
        internal var mVersion: String? = null
        internal var mCreator: String? = null
        internal var mMetadata: Metadata? = null

        fun setTracks(tracks: List<Track>): Builder {
            mTracks = tracks
            return this
        }

        fun setWayPoints(wayPoints: List<WayPoint>): Builder {
            mWayPoints = wayPoints
            return this
        }

        fun setRoutes(routes: List<Route>): Builder {
            this.mRoutes = routes
            return this
        }

        fun setVersion(version: String): Builder {
            mVersion = version
            return this
        }

        fun setCreator(creator: String): Builder {
            mCreator = creator
            return this
        }

        fun setMetadata(mMetadata: Metadata): Builder {
            this.mMetadata = mMetadata
            return this
        }

        fun build(): Gpx {
            return Gpx(this)
        }


    }
}