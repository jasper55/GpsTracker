package wagner.jasper.gpstracker.gpxparser

import java.util.Collections.unmodifiableList
import kotlin.collections.ArrayList


class Route private constructor(builder: Builder) {
    val routePoints: List<RoutePoint>
    val routeName: String?
    val routeDesc: String?
    val routeCmt: String?
    val routeSrc: String?
    val routeNumber: Int?
    val routeLink: Link?
    val routeType: String?

    init {
        routePoints = unmodifiableList(ArrayList(builder.mRoutePoints!!))
        routeName = builder.mRouteName
        routeDesc = builder.mRouteDesc
        routeCmt = builder.mRouteCmt
        routeSrc = builder.mRouteSrc
        routeNumber = builder.mRouteNumber
        routeLink = builder.mRouteLink
        routeType = builder.mRouteType
    }

    class Builder {
        internal var mRoutePoints: List<RoutePoint>? = null
        internal var mRouteName: String? = null
        internal var mRouteDesc: String? = null
        internal var mRouteCmt: String? = null
        internal var mRouteSrc: String? = null
        internal var mRouteNumber: Int? = null
        internal var mRouteLink: Link? = null
        internal var mRouteType: String? = null

        fun setRoutePoints(routePoints: List<RoutePoint>): Builder {
            mRoutePoints = routePoints
            return this
        }

        fun setRouteName(routeName: String): Builder {
            mRouteName = routeName
            return this
        }

        fun setRouteDesc(routeDesc: String): Builder {
            mRouteDesc = routeDesc
            return this
        }

        fun setRouteCmt(routeCmt: String): Builder {
            mRouteCmt = routeCmt
            return this
        }

        fun setRouteSrc(routeSrc: String): Builder {
            mRouteSrc = routeSrc
            return this
        }

        fun setRouteNumber(routeNumber: Int?): Builder {
            mRouteNumber = routeNumber
            return this
        }

        fun setRouteLink(routeLink: Link): Builder {
            mRouteLink = routeLink
            return this
        }

        fun setRouteType(routeType: String): Builder {
            mRouteType = routeType
            return this
        }

        fun build(): Route {
            return Route(this)
        }
    }
}