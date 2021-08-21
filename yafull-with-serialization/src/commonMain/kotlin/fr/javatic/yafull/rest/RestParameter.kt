package fr.javatic.yafull.rest

import fr.javatic.util.RFC7230
import fr.javatic.util.serializableOpenapiSchema
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.reflect.KType

sealed class RestParameter(
    protected val typeOf: KType,
    internal val name: String,
    private val description: String?,
    private val format: ParameterFormat,
) {
    protected abstract val openapiParameterInValue: String

    internal fun createOpenapi(
        schemasInfoHolder: OpenapiSpecFactory.SchemasInfoHolder,
    ): JsonObject = buildJsonObject {
        put("name", name)
        put("in", openapiParameterInValue)
        put("required", !typeOf.isMarkedNullable)
        if (description != null) {
            put("description", description)
        }
        put("style", format.style)
        put("explode", format.explode)
        put("schema", typeOf.serializableOpenapiSchema(schemasInfoHolder))
    }

    class Header<T> internal constructor(
        typeOf: KType,
        name: String,
        description: String?,
        private val format: ParameterFormat.Header
    ) : RestParameter(typeOf, name, description, format) {
        override val openapiParameterInValue: String = "header"

        init {
            require(RFC7230.isToken(name)) { "Header name `$name` is not valid according to RFC7230 Section 3.2 and Section 3.2.6" }
            require(!name.equals("content-type", true)) { "Content-Type header is managed by response body declaration" }
        }

        internal fun encode(value: Any?, res: MutableList<Pair<String, String>> = mutableListOf()): List<Pair<String, String>> =
            format.encode(name, typeOf, value, res)

        internal fun decode(headers: List<Pair<String, String>>): T =
            format.decode(name, typeOf, headers)
    }

    class Path<T> internal constructor(
        typeOf: KType,
        name: String,
        description: String?,
        private val format: ParameterFormat.Path
    ) : RestParameter(typeOf, name, description, format) {
        override val openapiParameterInValue: String = "path"

        init {
            require(name.all { it.isLetterOrDigit() || it == '_' }) { "Path parameter must be composed of letter or digit or '_'" }
        }

        internal fun encode(value: Any?): String = format.encode(typeOf, value)
        internal fun decode(value: String?): T = format.decode(typeOf, value)
    }

    class Query<T> internal constructor(
        typeOf: KType,
        name: String,
        description: String?,
        private val format: ParameterFormat.Query
    ) : RestParameter(typeOf, name, description, format) {
        override val openapiParameterInValue: String = "query"

        internal fun encode(value: Any?): String = format.encode(name, typeOf, value)
        internal fun decode(values: Map<String, List<String>>?): T = format.decode(name, typeOf, values)
    }
}
