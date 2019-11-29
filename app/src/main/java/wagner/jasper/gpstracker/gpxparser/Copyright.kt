package wagner.jasper.gpstracker.gpxparser

class Copyright private constructor(builder: Builder) {

    val author: String?
    val year: Int?
    val license: String?

    init {
        author = builder.mAuthor
        year = builder.mYear
        license = builder.mLicense
    }

    class Builder {
        internal var mAuthor: String? = null
        internal var mYear: Int? = null
        internal var mLicense: String? = null

        fun setAuthor(author: String): Builder {
            mAuthor = author
            return this
        }

        fun setYear(year: Int?): Builder {
            mYear = year
            return this
        }

        fun setLicense(license: String): Builder {
            mLicense = license
            return this
        }

        fun build(): Copyright {
            return Copyright(this)
        }
    }
}