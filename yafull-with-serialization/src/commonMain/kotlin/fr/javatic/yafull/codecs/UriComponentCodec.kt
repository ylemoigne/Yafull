package fr.javatic.yafull.codecs

import fr.javatic.kotlinSdkExtensions.UriContext
import fr.javatic.kotlinSdkExtensions.uriComponentDecoded
import fr.javatic.kotlinSdkExtensions.uriComponentEncoded
import kotlinx.serialization.SerializationException
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import kotlin.reflect.KType
import kotlin.reflect.typeOf

internal object UriComponentCodec {
    inline fun <reified T> encode(value: T, ctx: UriContext? = null) = encode(typeOf<T>(), value, ctx)

    fun encode(type: KType, value: Any?, ctx: UriContext? = null): String {
        return encode(serializer(type), value, ctx)
    }

    fun <T> encode(serializer: SerializationStrategy<T>, value: T, ctx: UriContext? = null): String {
        val uriEncoder = Encoder(ctx)
        uriEncoder.encodeSerializableValue(serializer, value)
        return uriEncoder.value
    }

    inline fun <reified T> decode(value: String): T = decode(typeOf<T>(), value)

    fun <T> decode(type: KType, value: String?): T {
        val uriDecoder = Decoder(value)

        @Suppress("UNCHECKED_CAST")
        return uriDecoder.decodeSerializableValue(serializer(type)) as T
    }

    private open class Encoder(private val ctx: UriContext?) : AbstractEncoder() {
        override val serializersModule: SerializersModule = EmptySerializersModule

        lateinit var value: String
            private set

        override fun encodeBoolean(value: Boolean) {
            this.value = value.toString()
        }

        override fun encodeByte(value: Byte) {
            this.value = value.toString()
        }

        override fun encodeShort(value: Short) {
            this.value = value.toString()
        }

        override fun encodeInt(value: Int) {
            this.value = value.toString()
        }

        override fun encodeLong(value: Long) {
            this.value = value.toString()
        }

        override fun encodeFloat(value: Float) {
            this.value = value.toString()
        }

        override fun encodeDouble(value: Double) {
            this.value = value.toString()
        }

        override fun encodeChar(value: Char) {
            this.value = value.toString().uriComponentEncoded(ctx)
        }

        override fun encodeString(value: String) {
            this.value = value.uriComponentEncoded(ctx)
        }

        override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
            value =
                enumDescriptor.getElementName(index).removePrefix(enumDescriptor.serialName).uriComponentEncoded(ctx)
        }

        override fun encodeNull() {
            value = ""
        }

        override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
            if (value == null)
                this.value = ""
            else
                super.encodeSerializableValue(serializer, value)
        }

        override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
            throw SerializationException("Structures are not supported by this encoder")
        }
    }

    private open class Decoder(input: String?) : AbstractDecoder() {
        override fun decodeSequentially(): Boolean = true

        override val serializersModule: SerializersModule = EmptySerializersModule

        private val _input = input ?: ""
        private var elementIndex = 0
        override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
            if (elementIndex == descriptor.elementsCount) return CompositeDecoder.DECODE_DONE
            return elementIndex++
        }

        override fun decodeBoolean(): Boolean = try {
            _input.toBooleanStrict()
        } catch (t: Throwable) {
            throw SerializationException(t)
        }

        override fun decodeByte(): Byte = try {
            _input.toByte()
        } catch (t: Throwable) {
            throw SerializationException(t)
        }

        override fun decodeShort(): Short = try {
            _input.toShort()
        } catch (t: Throwable) {
            throw SerializationException(t)
        }

        override fun decodeInt(): Int = try {
            _input.toInt()
        } catch (t: Throwable) {
            throw SerializationException(t)
        }

        override fun decodeLong(): Long = try {
            _input.toLong()
        } catch (t: Throwable) {
            throw SerializationException(t)
        }

        override fun decodeFloat(): Float = try {
            _input.toFloat()
        } catch (t: Throwable) {
            throw SerializationException(t)
        }

        override fun decodeDouble(): Double = try {
            _input.toDouble()
        } catch (t: Throwable) {
            throw SerializationException(t)
        }

        override fun decodeChar(): Char = try {
            _input.uriComponentDecoded()[0]
        } catch (t: Throwable) {
            throw SerializationException(t)
        }

        override fun decodeString(): String = try {
            _input.uriComponentDecoded()
        } catch (t: Throwable) {
            throw SerializationException(t)
        }

        override fun decodeNotNullMark(): Boolean = _input.isEmpty()

        override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
            try {
                val decoded = _input.uriComponentDecoded()
                for (i in 0 until enumDescriptor.elementsCount) {
                    if (decoded == enumDescriptor.getElementName(i).removePrefix(enumDescriptor.serialName)) {
                        return i
                    }
                }
                throw SerializationException("Failed to find enum value `$decoded` in enum `${enumDescriptor.serialName}`")
            } catch (t: Throwable) {
                if (t is SerializationException) throw t
                throw SerializationException(t)
            }
        }

        override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
            throw SerializationException("Structures are not supported by this decoder")
        }
    }

}
