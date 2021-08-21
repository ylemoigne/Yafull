package fr.javatic.yafull.router

import fr.javatic.yafull.rest.ParameterFormat
import kotlin.reflect.KType

class BrowserParameterPath<T>(
    private val typeOf: KType,
    val name: String,
    val format: ParameterFormat.Path
) {
    init {
        require(name.all { it.isLetterOrDigit() || it == '_' }) { "Path parameter must be composed of letter or digit or '_'" }
    }

    fun encode(value: Any?): String = format.encode(typeOf, value)
    fun decode(value: String?): T = format.decode(typeOf, value)
}
