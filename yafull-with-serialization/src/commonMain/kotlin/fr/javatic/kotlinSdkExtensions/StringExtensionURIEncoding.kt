package fr.javatic.kotlinSdkExtensions

import fr.javatic.jdklike.io.ByteArrayOutputStream
import fr.javatic.jdklike.util.BitSet

enum class UriContext {
    HEADER_VALUE_ITEM,
    QUERY_PARAM
}

fun String.uriComponentEncoded(ctx: UriContext?, optimistic: Boolean = true): String {
    if (isEmpty()) return this

    val pchar = allowedChars(ctx)

    val chars = toCharArray()
    if (optimistic) {
        var missplacedOptimism = false
        for (c in chars) {
            if (!pchar.get(c.code)) {
                missplacedOptimism = true
                break
            }
        }
        if (!missplacedOptimism) return this
    }

    val baos = ByteArrayOutputStream(2 * chars.size)
    for (c in chars) {
        val charCode = c.code
        if (pchar.get(charCode)) {
            baos.write(charCode)
        } else {
            baos.write(percentCode)
            baos.write(Char.forDigit(charCode shr 4 and 0xF, 16).uppercaseChar().code)
            baos.write(Char.forDigit(charCode and 0xF, 16).uppercaseChar().code)
        }
    }
    return baos.toString()
}

fun String.uriComponentDecoded(): String {
    if (isEmpty()) return this

    val baos = ByteArrayOutputStream(length)
    var shortcutPossible = true
    var i = 0
    while (i < length) {
        val ch = get(i).code
        if (ch == percentCode) {
            if (i + 2 < length) {
                val hex1 = get(i + 1)
                val hex2 = get(i + 2)
                val u = hex1.digitToIntOrNull(16) ?: -1
                val l = hex2.digitToIntOrNull(16) ?: -1
                require(!(u == -1 || l == -1)) { "Invalid encoded sequence \"" + substring(i) + "\"" }
                baos.write(((u shl 4) + l).toChar().code)
                i += 2
                shortcutPossible = false
            } else {
                throw IllegalArgumentException("Invalid encoded sequence \"" + substring(i) + "\"")
            }
        } else {
            baos.write(ch)
        }
        i++
    }

    if (shortcutPossible) return this

    return baos.toString()
}

private fun allowedChars(ctx: UriContext?): BitSet = when (ctx) {
    UriContext.HEADER_VALUE_ITEM -> BitSet().apply {
        or(BASE_ALLOW)
        or(AMPERSAND)
        or(EQUALS)
    }
    UriContext.QUERY_PARAM -> BitSet().apply {
        or(BASE_ALLOW)
        or(COMMA)
    }
    null -> BitSet().apply {
        or(BASE_ALLOW)
        or(COMMA)
        or(AMPERSAND)
        or(EQUALS)
    }
}

private const val percentCode = '%'.code

private val COMMA = BitSet().apply {
    set(','.code)
}
private val AMPERSAND = BitSet().apply {
    set('&'.code)
}
private val EQUALS = BitSet().apply {
    set('='.code)
}
private val BASE_ALLOW = BitSet().apply {
    set('a'.code, 'z'.code)
    set('A'.code, 'Z'.code)
    set('0'.code, '9'.code)
    set(';'.code)
    // set(','.code)
    // set('/'.code)
    // set('?'.code)
    set(':'.code)
    set('@'.code)
    // set('&'.code)
    // set('='.code)
    set('+'.code)
    set('$'.code)
    set('-'.code)
    set('_'.code)
    set('.'.code)
    set('!'.code)
    set('~'.code)
    set('*'.code)
    set('\''.code)
    set('('.code)
    set(')'.code)
    // set('#'.code)
}

