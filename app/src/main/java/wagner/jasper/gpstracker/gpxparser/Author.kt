package wagner.jasper.gpstracker.gpxparser

class Author private constructor(builder: Builder) {

    val name: String?
    val email: Email?
    val link: Link?

    init {
        this.name = builder.mName
        this.email = builder.mEmail
        this.link = builder.mLink
    }

    class Builder {
        internal var mName: String? = null
        internal var mEmail: Email? = null
        internal var mLink: Link? = null

        fun setName(name: String): Builder {
            mName = name
            return this
        }

        fun setEmail(email: Email): Builder {
            mEmail = email
            return this
        }

        fun setLink(link: Link): Builder {
            mLink = link
            return this
        }

        fun build(): Author {
            return Author(this)
        }
    }
}