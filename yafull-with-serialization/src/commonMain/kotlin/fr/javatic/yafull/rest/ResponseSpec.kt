package fr.javatic.yafull.rest

import fr.javatic.util.serializableOpenapiSchema
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.*
import kotlinx.serialization.serializer
import kotlin.reflect.KType
import kotlin.reflect.typeOf

sealed class ResponseSpec {
    abstract val status: Int
    abstract val statusMessage: String?
    abstract val declaredHeaders: Set<ResponseHeader<*>>

    internal abstract fun createOpenapi(schemasInfoHolder: OpenapiSpecFactory.SchemasInfoHolder): JsonObject
    internal abstract fun matchCodeAndMime(status: Int, mimeType: String?): Boolean

    internal fun <T> checkIsDeclared(responseHeader: ResponseHeader<T>) {
        if (!declaredHeaders.contains(responseHeader)) throw Error("Response header `${responseHeader.name}` is not declared in response")
    }

    class WithBody<TBody>(
        override val status: Int,
        override val statusMessage: String?,
        override val declaredHeaders: Set<ResponseHeader<*>>,
        val mimeType: String,
        private val bodyDescription: String?,
        val examples: Map<String, TBody> = emptyMap(),
        private val bodyType: KType,
        private val encoder: (TBody) -> ByteArray,
        private val decoder: (ByteArray) -> TBody
    ) : ResponseSpec() {
        override fun createOpenapi(schemasInfoHolder: OpenapiSpecFactory.SchemasInfoHolder) = buildJsonObject {
            bodyDescription?.let { put("description", it) }
            put("content", buildJsonObject {
                put(mimeType, buildJsonObject {
                    put("schema", bodyType.serializableOpenapiSchema(schemasInfoHolder))
                })
            })
        }

        override fun matchCodeAndMime(status: Int, mimeType: String?): Boolean {
            return this.status == status && this.mimeType == mimeType
        }

        fun encode(content: TBody): ByteArray = encoder(content)
        fun decode(content: ByteArray): TBody = decoder(content)

        override fun toString(): String {
            return "ResponseSpec.WithBody(status=$status, mimeType='$mimeType', bodyType=$bodyType)"
        }
    }

    class Base(
        override val status: Int,
        override val statusMessage: String?,
        val description: String?,
        override val declaredHeaders: Set<ResponseHeader<*>>,
        val json: Json
    ) : ResponseSpec() {
        val responses = mutableMapOf<String, WithBody<*>>()

        override fun createOpenapi(schemasInfoHolder: OpenapiSpecFactory.SchemasInfoHolder) = buildJsonObject {
            if (description != null) {
                put("description", description)
            }
            if (responses.isNotEmpty()) {
                putJsonObject("content") {
                    for (resp in responses.values) {
                        put(resp.mimeType, resp.createOpenapi(schemasInfoHolder))
                    }
                }
            }
        }

        override fun matchCodeAndMime(status: Int, mimeType: String?): Boolean {
            return this.status == status && mimeType == null
        }

        fun withStringBody(
            bodyDescription: String = "No Description",
            examples: Map<String, String> = emptyMap(),
            mimeType: MimeTypeBuilder.() -> String = { TEXT_PLAIN }
        ): WithBody<String> {
            val builder = MimeTypeBuilder()
            val computedMimeType = builder.mimeType()
            val responseWithBodySpec = WithBody(
                status,
                statusMessage,
                declaredHeaders,
                computedMimeType,
                bodyDescription,
                examples,
                typeOf<String>(),
                { s -> s.encodeToByteArray() },
                { b -> b.decodeToString() })

            if (responses.put(computedMimeType, responseWithBodySpec) != null)
                throw Error("A response body of mime type `$computedMimeType` is already registered")

            return responseWithBodySpec
        }

        fun withBytesBody(
            bodyDescription: String = "No Description",
            mimeType: MimeTypeBuilder.() -> String = { APPLICATION_OCTET_STREAM }
        ): WithBody<ByteArray> {
            val builder = MimeTypeBuilder()
            val computedMimeType = builder.mimeType()
            val responseWithBodySpec = WithBody(
                status,
                statusMessage,
                declaredHeaders,
                computedMimeType,
                bodyDescription,
                emptyMap(),
                typeOf<ByteArray>(),
                { b -> b },
                { b -> b })

            if (responses.put(computedMimeType, responseWithBodySpec) != null)
                throw Error("A response body of mime type `$computedMimeType` is already registered")

            return responseWithBodySpec
        }

        inline fun <reified TBody> withJsonBody(
            bodyDescription: String = "No Description",
            examples: Map<String, TBody> = emptyMap(),
            noinline mimeType: MimeTypeBuilder.() -> String = { APPLICATION_JSON }
        ): WithBody<TBody> = withJsonBody(typeOf<TBody>(), bodyDescription, examples, mimeType)

        fun <TBody> withJsonBody(
            typeOf: KType,
            bodyDescription: String = "No Description",
            examples: Map<String, TBody> = emptyMap(),
            mimeType: MimeTypeBuilder.() -> String = { APPLICATION_JSON }
        ): WithBody<TBody> {
            val builder = MimeTypeBuilder()
            val computedMimeType = builder.mimeType()
            val responseWithBodySpec = WithBody(
                status,
                statusMessage,
                declaredHeaders,
                computedMimeType,
                bodyDescription,
                examples,
                typeOf,
                { s ->
                    json.encodeToString(serializer(typeOf), s).encodeToByteArray()
                },
                { b ->
                    @Suppress("UNCHECKED_CAST")
                    json.decodeFromString(serializer(typeOf) as KSerializer<TBody>, b.decodeToString())
                })

            if (responses.put(computedMimeType, responseWithBodySpec) != null)
                throw Error("A request body of mime type `$computedMimeType` is already registered")

            return responseWithBodySpec
        }

        override fun toString(): String {
            return "ResponseSpec.Base(status=$status)"
        }
    }

}
