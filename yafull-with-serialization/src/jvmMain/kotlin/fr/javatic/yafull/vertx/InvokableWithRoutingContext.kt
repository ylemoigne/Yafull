package fr.javatic.yafull.vertx

import fr.javatic.yafull.rest.*
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.RoutingContext

operator fun <T> RestParameter.Path<T>.invoke(ctx: RoutingContext): T =
    decode(ctx.pathParam(this.name))

operator fun <T> RestParameter.Query<T>.invoke(ctx: RoutingContext): T =
    decode(ctx.queryParams().toMap())

operator fun <T> RestParameter.Header<T>.invoke(ctx: RoutingContext): T =
    decode(ctx.request().headers().getAll(name).map { name to it })

operator fun RequestBodyUntyped.invoke(ctx: RoutingContext): ByteArray =
    ctx.body.bytes

operator fun <T> RequestBodyWithMime<T>.invoke(ctx: RoutingContext): T =
    decode(ctx.body.bytes)

operator fun ResponseSpec.Base.invoke(
    ctx: RoutingContext,
    block: (VertxResponseBuilder.() -> Any?)? = null
) {
    val headerValues = mutableMapOf<ResponseHeader<*>, Any?>()
    if (block != null) {
        val builder = VertxResponseBuilder(this, headerValues)
        builder.block()
    }

    val vertxResponse = ctx.response()
    vertxResponse.statusCode = status
    statusMessage?.let { vertxResponse.setStatusMessage(it) }
    handleHeaders(headerValues, vertxResponse)

    vertxResponse.end()
}

operator fun <TBody> ResponseSpec.WithBody<TBody>.invoke(
    ctx: RoutingContext,
    block: VertxResponseBuilder.() -> TBody
) {
    val headerValues = mutableMapOf<ResponseHeader<*>, Any?>()
    val builder = VertxResponseBuilder(this, headerValues)
    val bodyContent = builder.block()

    val vertxResponse = ctx.response()
    vertxResponse.statusCode = status
    statusMessage?.let { vertxResponse.setStatusMessage(it) }
    handleHeaders(headerValues, vertxResponse)

    vertxResponse.putHeader("content-type", mimeType)
    vertxResponse.end(Buffer.buffer(encode(bodyContent)))
}

class VertxResponseBuilder(
    private val responseSpec: ResponseSpec,
    private val headerValues: MutableMap<ResponseHeader<*>, Any?>
) {
    @Suppress("UNCHECKED_CAST")
    var <T> ResponseHeader<T>.value: T
        get() {
            responseSpec.checkIsDeclared(this)
            return headerValues[this] as T
        }
        set(value) {
            responseSpec.checkIsDeclared(this)
            headerValues[this] = value
        }
}

private fun ResponseSpec.handleHeaders(
    headerValues: Map<ResponseHeader<*>, Any?>,
    vertxResponse: HttpServerResponse
) {
    val missingValueHeaders = this.declaredHeaders
        .asSequence()
        .filter { !it.typeOf.isMarkedNullable }
        .filter { headerValues[it] == null }
        .map { it.name }
        .toSet()
    if (missingValueHeaders.isNotEmpty())
        throw Error("The following non nullable header does not have value `$missingValueHeaders`")

    val undeclaredHeaderWithValue =
        headerValues.keys.filter { !this.declaredHeaders.contains(it) }.map { it.name }.toSet()
    if (undeclaredHeaderWithValue.isNotEmpty())
        throw Error("The following header have been set but are not present in declaration `$undeclaredHeaderWithValue`")

    for ((header, value) in headerValues.entries) {
        for ((headerName, headerValue) in header.encode(value)) {
            vertxResponse.putHeader(headerName, headerValue)
        }
    }
}
