package fr.javatic.yafull.rest

import kotlin.reflect.KType

class ResponseHeader<T> internal constructor(
    internal val typeOf: KType,
    internal val name: String,
    description: String?,
    private val format: ParameterFormat.Header
) {
    internal fun encode(value: Any?): List<Pair<String, String>> = format.encode(name, typeOf, value)
}
