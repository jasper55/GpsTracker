package wagner.jasper.gpstracker.gpxparser

class Email private constructor(builder: Builder) {

    val id: String?
    val domain: String?

    init {
        this.id = builder.mId
        this.domain = builder.mDomain
    }

    class Builder {
        internal var mId: String? = null
        internal var mDomain: String? = null

        fun setId(id: String): Builder {
            mId = id
            return this
        }

        fun setDomain(domain: String): Builder {
            mDomain = domain
            return this
        }

        fun build(): Email {
            return Email(this)
        }
    }
}