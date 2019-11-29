package wagner.jasper.gpstracker.gpxparser

class Bounds private constructor(builder: Builder) {

    val minLat: Double?
    val minLon: Double?
    val maxLat: Double?
    val maxLon: Double?

    init {
        minLat = builder.mMinLat
        minLon = builder.mMinLon
        maxLat = builder.mMaxLat
        maxLon = builder.mMaxLon
    }

    class Builder {
        internal var mMinLat: Double? = null
        internal var mMinLon: Double? = null
        internal var mMaxLat: Double? = null
        internal var mMaxLon: Double? = null

        fun setMinLat(minLat: Double?): Builder {
            mMinLat = minLat
            return this
        }

        fun setMinLon(minLon: Double?): Builder {
            mMinLon = minLon
            return this
        }

        fun setMaxLat(maxLat: Double?): Builder {
            mMaxLat = maxLat
            return this
        }

        fun setMaxLon(maxLon: Double?): Builder {
            mMaxLon = maxLon
            return this
        }

        fun build(): Bounds {
            return Bounds(this)
        }
    }
}