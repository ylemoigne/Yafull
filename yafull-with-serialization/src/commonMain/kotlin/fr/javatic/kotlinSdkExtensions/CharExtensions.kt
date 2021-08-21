package fr.javatic.kotlinSdkExtensions

val Char.Companion.MIN_RADIX get() = 2
val Char.Companion.MAX_RADIX get() = 36

fun Char.Companion.forDigit(digit: Int, radix: Int): Char {
    if (digit >= radix || digit < 0) {
        return '\u0000'
    }
    if (radix < MIN_RADIX || radix > MAX_RADIX) {
        return '\u0000'
    }
    return if (digit < 10)
        ('0'.code + digit).toChar()
    else
        ('a'.code - 10 + digit).toChar()
}
