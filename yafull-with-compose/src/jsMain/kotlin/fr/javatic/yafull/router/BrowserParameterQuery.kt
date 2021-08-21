package fr.javatic.yafull.router

import fr.javatic.yafull.rest.ParameterFormat
import kotlin.reflect.KType

class BrowserParameterQuery<T>(
    private val typeOf: KType,
    val name: String,
    val format: ParameterFormat.Query
) {
    init {
        require(name.all { it.isLetterOrDigit() || it == '_' }) { "Path parameter must be composed of letter or digit or '_'" }
    }

    internal fun encode(value: Any?): String = format.encode(name, typeOf, value)
    internal fun <T> decode(values: Map<String, List<String>>?): T = format.decode(name, typeOf, values)
}
