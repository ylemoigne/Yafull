package fr.javatic.kotlinSdkExtensions

import kotlin.time.Duration

private fun Int.asNullSingularOrPluralOf(word: String): String? = when (this) {
    0 -> null
    1 -> "1 $word"
    else -> "$this ${word}s"
}

fun Duration.toHumanString(): String = buildList {
    toComponents { days, hours, minutes, seconds, nanoseconds ->
        val years: Int = (days / 365)
        val restDay: Int = (days % 365)
        years.asNullSingularOrPluralOf("year")?.let { add(it) }
        restDay.asNullSingularOrPluralOf("day")?.let { add(it) }
        hours.asNullSingularOrPluralOf("hour")?.let { add(it) }
        if (years < 1) {
            minutes.asNullSingularOrPluralOf("minute")?.let { add(it) }
            if (restDay < 1) {
                seconds.asNullSingularOrPluralOf("second")?.let { add(it) }
                if (hours < 1) {
                    (nanoseconds / 1e6).toInt().asNullSingularOrPluralOf("millis")?.let { add(it) }
                }
            }
        }
    }
}.joinToString(" ")
