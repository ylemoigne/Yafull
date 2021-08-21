package fr.javatic.yafull.router

object HttpUtils {
    fun normalizePath(pathname: String): String {
        // add trailing slash if not set
        if (pathname.length == 0) {
            return "/"
        }
        val ibuf = StringBuilder(pathname.length + 1)

        // Not standard!!!
        if (pathname[0] != '/') {
            ibuf.append('/')
        }
        ibuf.append(pathname)
        var i = 0
        while (i < ibuf.length) {
            // decode unreserved chars described in
            // http://tools.ietf.org/html/rfc3986#section-2.4
            if (ibuf[i] == '%') {
                decodeUnreserved(ibuf, i)
            }
            i++
        }

        // remove dots as described in
        // http://tools.ietf.org/html/rfc3986#section-5.2.4
        return removeDots(ibuf)
    }

    private fun decodeUnreserved(path: StringBuilder, start: Int) {
        if (start + 3 <= path.length) {
            // these are latin chars so there is no danger of falling into some special unicode char that requires more
            // than 1 byte
            val escapeSequence = path.substring(start + 1, start + 3)
            val unescaped: Int
            try {
                unescaped = escapeSequence.toInt(16)
                require(unescaped >= 0) { "Invalid escape sequence: %$escapeSequence" }
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("Invalid escape sequence: %$escapeSequence")
            }
            // validate if the octet is within the allowed ranges
            if ( // ALPHA
                unescaped >= 0x41 && unescaped <= 0x5A ||
                unescaped >= 0x61 && unescaped <= 0x7A ||  // DIGIT
                unescaped >= 0x30 && unescaped <= 0x39 ||  // HYPHEN
                unescaped == 0x2D ||  // PERIOD
                unescaped == 0x2E ||  // UNDERSCORE
                unescaped == 0x5F ||  // TILDE
                unescaped == 0x7E
            ) {
                path[start] = unescaped.toChar()
                path.deleteRange(start + 1, start + 3)
            }
        } else {
            throw IllegalArgumentException("Invalid position for escape character: $start")
        }
    }

    fun removeDots(path: CharSequence): String {
        var pathVar = path
        val obuf = StringBuilder(pathVar.length)
        var i = 0
        while (i < pathVar.length) {
            // remove dots as described in
            // http://tools.ietf.org/html/rfc3986#section-5.2.4
            if (matches(pathVar, i, "./")) {
                i += 2
            } else if (matches(pathVar, i, "../")) {
                i += 3
            } else if (matches(pathVar, i, "/./")) {
                // preserve last slash
                i += 2
            } else if (matches(pathVar, i, "/.", true)) {
                pathVar = "/"
                i = 0
            } else if (matches(pathVar, i, "/../")) {
                // preserve last slash
                i += 3
                val pos = obuf.lastIndexOf("/")
                if (pos != -1) {
                    obuf.deleteRange(pos, obuf.length)
                }
            } else if (matches(pathVar, i, "/..", true)) {
                pathVar = "/"
                i = 0
                val pos = obuf.lastIndexOf("/")
                if (pos != -1) {
                    obuf.deleteRange(pos, obuf.length)
                }
            } else if (matches(pathVar, i, ".", true) || matches(pathVar, i, "..", true)) {
                break
            } else {
                if (pathVar[i] == '/') {
                    i++
                    // Not standard!!!
                    // but common // -> /
                    if (obuf.length == 0 || obuf[obuf.length - 1] != '/') {
                        obuf.append('/')
                    }
                }
                val pos: Int = indexOfSlash(pathVar, i)
                i = if (pos != -1) {
                    obuf.append(pathVar, i, pos)
                    pos
                } else {
                    obuf.append(pathVar, i, pathVar.length)
                    break
                }
            }
        }
        return obuf.toString()
    }

    private fun indexOfSlash(str: CharSequence, start: Int): Int {
        for (i in start until str.length) {
            if (str[i] == '/') {
                return i
            }
        }
        return -1
    }

    private fun matches(path: CharSequence, start: Int, what: String): Boolean {
        return matches(path, start, what, false)
    }

    private fun matches(path: CharSequence, start: Int, what: String, exact: Boolean): Boolean {
        if (exact) {
            if (path.length - start != what.length) {
                return false
            }
        }
        if (path.length - start >= what.length) {
            for (i in 0 until what.length) {
                if (path[start + i] != what[i]) {
                    return false
                }
            }
            return true
        }
        return false
    }
}
