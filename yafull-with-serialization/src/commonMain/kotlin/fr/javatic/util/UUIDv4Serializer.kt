package fr.javatic.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object UUIDv4Serializer : KSerializer<UUIDv4> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UUIDv4", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UUIDv4 = UUIDv4(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: UUIDv4) = encoder.encodeString(value.value)
}
