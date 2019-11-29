package wagner.jasper.gpstracker.gpxparser

import org.joda.time.DateTime

abstract class Point internal constructor(builder: Builder) {
    /**
     * @return the latitude in degrees
     */
    val latitude: Double?
    /**
     * @return the longitude in degrees
     */
    val longitude: Double?
    /**
     * @return the elevation in meters
     */
    val elevation: Double?
    val time: DateTime?
    /**
     * @return the point name
     */
    val name: String?
    /**
     * @return the description
     */
    val desc: String?
    /**
     * @return the type (category)
     */
    val type: String?

    init {
        latitude = builder.mLatitude
        longitude = builder.mLongitude
        elevation = builder.mElevation
        time = builder.mTime
        name = builder.mName
        desc = builder.mDesc
        type = builder.mType
    }

    abstract class Builder {
        internal var mLatitude: Double? = null
        internal var mLongitude: Double? = null
        internal var mElevation: Double? = null
        internal var mTime: DateTime? = null
        internal var mName: String? = null
        internal var mDesc: String? = null
        internal var mType: String? = null

        fun setLatitude(latitude: Double?): Builder {
            mLatitude = latitude
            return this
        }

        fun setLongitude(longitude: Double?): Builder {
            mLongitude = longitude
            return this
        }

        fun setElevation(elevation: Double?): Builder {
            mElevation = elevation
            return this
        }

        fun setTime(time: DateTime): Builder {
            mTime = time
            return this
        }

        fun setName(mame: String): Builder {
            mName = mame
            return this
        }

        fun setDesc(desc: String): Builder {
            mDesc = desc
            return this
        }

        fun setType(type: String): Builder {
            mType = type
            return this
        }

        abstract fun build(): Point
    }
}