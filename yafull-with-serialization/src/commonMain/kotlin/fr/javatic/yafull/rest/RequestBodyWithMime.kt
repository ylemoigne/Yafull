package fr.javatic.yafull.rest

import kotlin.reflect.KType

class RequestBodyWithMime<T> internal constructor(
    override val mimeType: String,
    internal val typeOf: KType,
    private val encoder: (T) -> ByteArray,
    private val decoder: (ByteArray) -> T
) : RequestBody {
    internal fun encode(body: T): ByteArray = encoder(body)
    internal fun decode(bytes: ByteArray): T = decoder(bytes)
}
