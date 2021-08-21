package fr.javatic.yafull.codecs

import fr.javatic.kotlinSdkExtensions.UriContext
import fr.javatic.kotlinSdkExtensions.uriComponentDecoded
import fr.javatic.kotlinSdkExtensions.uriComponentEncoded
import kotlinx.serialization.SerializationException
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import kotlin.reflect.KType
import kotlin.reflect.typeOf

internal object QueryParameterFormCodec {
    inline fun <reified T> encode(parameterName: String, value: T, explode: Boolean) =
        encode(parameterName, typeOf<T>(), value, explode)

    fun encode(
        parameterName: String,
        valueType: KType,
        value: Any?,
        explode: Boolean
    ): String {
        val queryFormEncoder = Encoder(parameterName, explode)
        queryFormEncoder.encodeSerializableValue(serializer(valueType), value)
        return queryFormEncoder.value
    }

    fun <T> decode(
        parameterName: String,
        valueType: KType,
        queryString: String?
    ): T {
        val decoder = Decoder(parameterName, queryString)

        @Suppress("UNCHECKED_CAST")
        return decoder.decodeSerializableValue(serializer(valueType)) as T
    }

    fun <T> decode(
        parameterName: String,
        valueType: KType,
        values: Map<String, List<String>>?
    ): T {
        val decoder = Decoder(parameterName, values)

        @Suppress("UNCHECKED_CAST")
        return decoder.decodeSerializableValue(serializer(valueType)) as T
    }

    fun <T> decode(
        valueType: KType,
        values: List<String>?
    ): T {
        val decoder = Decoder(values)

        @Suppress("UNCHECKED_CAST")
        return decoder.decodeSerializableValue(serializer(valueType)) as T
    }

    private class Encoder(parameterName: String, private val explode: Boolean) : AbstractEncoder() {
        override val serializersModule: SerializersModule = EmptySerializersModule

        private var inCollection = false
        private val uriContext = if (!explode) UriContext.HEADER_VALUE_ITEM else UriContext.QUERY_PARAM
        private val encodedParameterName = parameterName.uriComponentEncoded(UriContext.QUERY_PARAM) + "="
        private val _values = mutableListOf<String>()
        val value
            get() = when {
                _values.isEmpty() -> ""
                explode -> _values.joinToString("&") { encodedParameterName + it }
                else -> encodedParameterName + _values.joinToString(",")
            }

        override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder {
            if (inCollection) throw SerializationException("Nested collection are not supported")
            inCollection = true
            return super.beginCollection(descriptor, collectionSize)
        }

        override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
            when (serializer.descriptor.kind) {
                is PrimitiveKind, SerialKind.ENUM -> {
                    if (value != null) {
                        _values.add(UriComponentCodec.encode(serializer, value))
                    }
                }
                is StructureKind.LIST -> super.encodeSerializableValue(serializer, value)
                else -> throw SerializationException("This encoder support only PrimitiveKind, SerialKind.ENUM and StructureKind.LIST")
            }
        }
    }

    private class Decoder(values: List<String>?) : AbstractDecoder() {
        constructor(parameterName: String, parameters: Map<String, List<String>>?) : this(
            parameters?.get(parameterName.uriComponentDecoded())
        )

        constructor(parameterName: String, queryString: String?) : this(
            queryString?.split('&')
                ?.asSequence()
                ?.map { it.split('=') }
                ?.onEach { if (it.size != 2) throw SerializationException("Query param must have exactly 2 part when split by `=` (find $it)") }
                ?.filter { it[0].uriComponentDecoded() == parameterName }
                ?.map { it[1] }
                ?.toList()
        )

        private val _values: MutableList<String> = values?.toMutableList() ?: mutableListOf()

        override val serializersModule: SerializersModule = EmptySerializersModule

        private var elementIndex = 0
        override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
            if (_values.size == 0) return CompositeDecoder.DECODE_DONE
            return elementIndex++
        }

        override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder =
            Decoder(_values)

        override fun decodeString(): String {
            return _values.removeAt(0).uriComponentDecoded()
        }
    }
}
