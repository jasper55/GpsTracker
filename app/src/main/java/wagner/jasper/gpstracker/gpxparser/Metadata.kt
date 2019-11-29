package wagner.jasper.gpstracker.gpxparser

import org.joda.time.DateTime

class Metadata private constructor(builder: Metadata.Builder) {

    val name: String?
    val desc: String?
    val author: Author?
    val copyright: Copyright?
    val link: Link?
    val time: DateTime?
    val keywords: String?
    val bounds: Bounds?
    val extensions: String?

    init {
        name = builder.mName
        desc = builder.mDesc
        author = builder.mAuthor
        copyright = builder.mCopyright
        link = builder.mLink
        time = builder.mTime
        keywords = builder.mKeywords
        bounds = builder.mBounds
        extensions = builder.mExtensions
    }

    class Builder {
        internal var mName: String? = null
        internal var mDesc: String? = null
        internal var mAuthor: Author? = null
        internal var mCopyright: Copyright? = null
        internal var mLink: Link? = null
        internal var mTime: DateTime? = null
        internal var mKeywords: String? = null
        internal var mBounds: Bounds? = null
        internal val mExtensions: String? = null

        fun setName(name: String): Builder {
            mName = name
            return this
        }

        fun setDesc(desc: String): Builder {
            mDesc = desc
            return this
        }

        fun setAuthor(author: Author): Builder {
            mAuthor = author
            return this
        }

        fun setCopyright(copyright: Copyright): Builder {
            mCopyright = copyright
            return this
        }

        fun setLink(link: Link): Builder {
            mLink = link
            return this
        }

        fun setTime(time: DateTime): Builder {
            mTime = time
            return this
        }

        fun setKeywords(keywords: String): Builder {
            mKeywords = keywords
            return this
        }

        fun setBounds(bounds: Bounds): Builder {
            mBounds = bounds
            return this
        }

        fun build(): Metadata {
            return Metadata(this)
        }


    }
}