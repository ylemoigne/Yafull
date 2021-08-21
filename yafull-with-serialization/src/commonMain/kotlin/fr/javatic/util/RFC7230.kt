package fr.javatic.util

import fr.javatic.jdklike.util.BitSet

object RFC7230 {
    val DIGIT = BitSet().apply {
        set('0'.code, '9'.code)
    }
    val ALPHA = BitSet().apply {
        set('a'.code, 'z'.code)
        set('A'.code, 'Z'.code)
    }

    val tchar = BitSet().apply {
        set('!'.code)
        set('#'.code)
        set('$'.code)
        set('%'.code)
        set('&'.code)
        set('\''.code)
        set('*'.code)
        set('+'.code)
        set('-'.code)
        set('.'.code)
        set('^'.code)
        set('_'.code)
        set('`'.code)
        set('|'.code)
        set('~'.code)

        or(DIGIT)
        or(ALPHA)
    }

    fun isTchar(c: Char) = tchar[c.code]
    fun isToken(s: String) = s.isNotBlank() && s.all(RFC7230::isTchar)
}
