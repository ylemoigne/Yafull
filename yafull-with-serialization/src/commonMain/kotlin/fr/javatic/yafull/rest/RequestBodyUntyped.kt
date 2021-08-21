package fr.javatic.yafull.rest

import fr.javatic.util.serializableOpenapiSchema
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import kotlinx.serialization.serializer
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class RequestBodyUntyped internal constructor(private val description: String?, val required: Boolean, val json: Json) : RequestBody {
    override val mimeType: String? = null

    internal val requestByMime = mutableMapOf<String, RequestBodyWithMime<*>>()

    internal fun createOpenapi(schemasInfoHolder: OpenapiSpecFactory.SchemasInfoHolder) = buildJsonObject {
        if (description != null) {
            put("description", description)
        }
        put("required", required)
        if (requestByMime.isNotEmpty()) {
            putJsonObject("content") {
                for (resp in requestByMime.values) {
                    putJsonObject(resp.mimeType) {
                        put("schema", resp.typeOf.serializableOpenapiSchema(schemasInfoHolder))
                    }
                }
            }
        }
    }

    fun stringContent(mimeType: MimeTypeBuilder.() -> String = { TEXT_PLAIN }): RequestBodyWithMime<String> {
        val builder = MimeTypeBuilder()
        val computedMimeType = builder.mimeType()
        val requestBodyWithMime = RequestBodyWithMime<String>(
            computedMimeType,
            typeOf<String>(),
            { it.encodeToByteArray() },
            { it.decodeToString() })
        if (requestByMime.put(computedMimeType, requestBodyWithMime) != null)
            throw Error("A request body with mime type `$computedMimeType` is already registered")

        return requestBodyWithMime
    }

    fun bytesContent(mimeType: MimeTypeBuilder.() -> String = { APPLICATION_OCTET_STREAM }): RequestBodyWithMime<ByteArray> {
        val builder = MimeTypeBuilder()
        val computedMimeType = builder.mimeType()
        val requestBodyWithMime =
            RequestBodyWithMime<ByteArray>(computedMimeType, typeOf<ByteArray>(), { b -> b }, { b -> b })
        if (requestByMime.put(computedMimeType, requestBodyWithMime) != null)
            throw Error("A request body with mime type `$computedMimeType` is already registered")

        return requestBodyWithMime
    }

    inline fun <reified T> jsonContent(noinline mimeType: MimeTypeBuilder.() -> String = { APPLICATION_JSON }): RequestBodyWithMime<T> =
        jsonContent(typeOf<T>(), mimeType)

    fun <T> jsonContent(typeOf: KType, mimeType: MimeTypeBuilder.() -> String = { APPLICATION_JSON }): RequestBodyWithMime<T> {
        val builder = MimeTypeBuilder()
        val computedMimeType = builder.mimeType()
        val requestBodyWithMime = RequestBodyWithMime<T>(
            computedMimeType,
            typeOf,
            { t -> json.encodeToString(serializer(typeOf), t).encodeToByteArray() },
            { s ->
                @Suppress("UNCHECKED_CAST")
                json.decodeFromString(serializer(typeOf) as KSerializer<T>, s.decodeToString())
            })

        if (requestByMime.put(computedMimeType, requestBodyWithMime) != null)
            throw Error("A request body with mime type `$computedMimeType` is already registered")

        return requestBodyWithMime
    }
}
