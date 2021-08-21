package fr.javatic.yafull.codecs

import fr.javatic.kotlinSdkExtensions.UriContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
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

internal object HeaderCodec {
    inline fun <reified T> encode(
        headerName: String,
        value: T,
        explode: Boolean,
        res: MutableList<Pair<String, String>> = mutableListOf()
    ) =
        encode(headerName, typeOf<T>(), value, explode, res)

    fun encode(
        headerName: String,
        valueType: KType,
        value: Any?,
        explode: Boolean,
        res: MutableList<Pair<String, String>> = mutableListOf()
    ): List<Pair<String, String>> {
        val uriEncoder = Encoder(headerName, explode, res)
        uriEncoder.encodeSerializableValue(serializer(valueType), value)
        return res
    }

    inline fun <reified T> decode(headerName: String, explode: Boolean, headers: List<Pair<String, String>>): T =
        decode(headerName, typeOf<T>(), explode, headers)

    fun <T> decode(headerName: String, valueType: KType, explode: Boolean, headers: List<Pair<String, String>>): T {
        val decoder = Decoder(headerName, explode, headers)

        @Suppress("UNCHECKED_CAST")
        return decoder.decodeSerializableValue(serializer(valueType)) as T
    }

    private class Encoder(
        private val headerName: String,
        private val explode: Boolean,
        private val res: MutableList<Pair<String, String>>
    ) : AbstractEncoder() {
        override val serializersModule: SerializersModule = EmptySerializersModule

        private var depth = 0
        private var firstElement = true
        private val headerValue = StringBuilder()

        override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
            depth += 1
            return super.beginStructure(descriptor)
        }

        override fun endStructure(descriptor: SerialDescriptor) {
            depth -= 1
            checkAndClose()
        }

        override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
            when (serializer.descriptor.kind) {
                is PrimitiveKind -> {
                    val encodedValue = UriComponentCodec.encode(serializer, value, UriContext.HEADER_VALUE_ITEM)
                    if (explode) {
                        res.add(headerName to encodedValue)
                    } else if (encodedValue.isNotEmpty()) {
                        if (!firstElement) {
                            headerValue.append(',')
                        }
                        headerValue.append(encodedValue)
                        firstElement = false
                    }
                    checkAndClose()
                }
                is StructureKind.LIST -> super.encodeSerializableValue(serializer, value)
                else -> throw SerializationException("This encoder support only PrimitiveKind and LIST kind")
            }
        }

        private fun checkAndClose() {
            if (depth == 0 && !explode) {
                res.add(headerName to headerValue.toString())
            }
        }
    }

    private class Decoder(
        private val explode: Boolean,
        private val matchingHeaders: MutableList<String>
    ) : AbstractDecoder() {
        constructor(headerName: String, explode: Boolean, headers: List<Pair<String, String>>) : this(
            explode,
            headers.asSequence()
                .filter { it.first.equals(headerName, true) }
                .map { it.second }
                .flatMap { if (explode) sequenceOf(it) else it.split(',').asSequence() }
                .toMutableList()
        )

        override val serializersModule: SerializersModule = EmptySerializersModule

        private var elementIndex = 0
        override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
            if (matchingHeaders.size == 0) return CompositeDecoder.DECODE_DONE
            return elementIndex++
        }

        override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder =
            Decoder(explode, matchingHeaders)

        override fun decodeString(): String {
            return UriComponentCodec.decode(matchingHeaders.removeAt(0))
        }
    }
}
