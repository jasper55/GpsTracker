package wagner.jasper.gpstracker.gpxparser

class Link private constructor(builder: Builder) {

    val href: String?
    val text: String?
    val type: String?

    init {
        href = builder.mLinkHref
        text = builder.mLinkText
        type = builder.mLinkType
    }

    class Builder {
        internal var mLinkHref: String? = null
        internal var mLinkText: String? = null
        internal var mLinkType: String? = null

        fun setLinkHref(linkHref: String): Builder {
            mLinkHref = linkHref
            return this
        }

        fun setLinkText(linkText: String): Builder {
            mLinkText = linkText
            return this
        }

        fun setLinkType(linkType: String): Builder {
            mLinkType = linkType
            return this
        }

        fun build(): Link {
            return Link(this)
        }
    }
}