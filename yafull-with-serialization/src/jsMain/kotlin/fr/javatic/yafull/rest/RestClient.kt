package fr.javatic.yafull.rest

import external.Headers
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.khronos.webgl.Int8Array
import org.w3c.dom.url.URLSearchParams
import org.w3c.fetch.RequestInit
import org.w3c.xhr.FormData

class RestClient(private val apiMountPoint: String, private val plugins: List<ClientApiPlugin>) {
    init {
        require(!apiMountPoint.endsWith('/')) { "Mount point must not end with '/'" }
    }

    inner class ClientRequestBuilder<TEndPoint : Endpoint>(
        private val endpoint: TEndPoint,
    ) {
        private var body: dynamic = null
        private var contentType: String? = null

        private val pathParametersValue = mutableMapOf<RestParameter.Path<*>, Any?>()
        private val queryParametersValue = mutableMapOf<RestParameter.Query<*>, Any?>()
        private val headerParametersValue = mutableMapOf<RestParameter.Header<*>, Any?>()

        init {
            require(apiMountPoint.startsWith('/')) { "Mount point must start with '/'" }
            require(!apiMountPoint.endsWith('/')) { "Mount point must not end with '/'" }
        }

        operator fun <T> RestParameter.Path<T>.invoke(value: T) {
            pathParametersValue[this] = value
        }

        operator fun <T> RestParameter.Query<T>.invoke(value: T) {
            queryParametersValue[this] = value
        }

        operator fun <T> RestParameter.Header<T>.invoke(value: T) {
            headerParametersValue[this] = value
        }

        operator fun RequestBodyUntyped.invoke(content: String) {
            body = content
        }

        operator fun RequestBodyUntyped.invoke(content: ByteArray) {
            body = content
        }

        operator fun RequestBodyUntyped.invoke(content: FormData) {
            body = content
        }

        operator fun RequestBodyUntyped.invoke(content: URLSearchParams) {
            body = content
        }

        operator fun <T> RequestBodyWithMime<T>.invoke(content: T) {
            contentType = mimeType
            body = encode(content)
        }

        private suspend fun computeMethod(): HttpMethod =
            plugins.fold(endpoint.method) { acc, plugin -> plugin.transformRequestMethod(endpoint, acc) }

        private suspend fun computePath(): String {
            val pathElements = plugins.fold(
                endpoint.declarePath(RestPathBuilder.create()).build()
            ) { acc, plugin -> plugin.transformRequestPath(endpoint, acc) }
            return pathElements.fold(StringBuilder(apiMountPoint).append('/')) { sb, el ->
                sb.append(
                    when (el) {
                        is RestPathElement.Literal -> el.value
                        is RestPathElement.Param -> el.restParameter.encode(pathParametersValue[el.restParameter])
                        is RestPathElement.Separator -> '/'
                    }
                )
            }.toString()
        }

        private suspend fun computeQuery(): String {
            val queryParameters = plugins.fold(queryParametersValue.toMap()) { acc, plugin -> plugin.transformRequestQuery(endpoint, acc) }

            val query = queryParameters.map { (param, value) ->
                param.encode(value)
            }.joinToString("&")

            return if (query.isEmpty()) query else "?$query"
        }

        private suspend fun computeHeaders(): Headers {
            val headers = mutableListOf<Pair<String, String>>()
            contentType?.let { headers.add("Content-Type" to it) }
            for ((header, value) in headerParametersValue) {
                header.encode(value, headers)
            }

            val finalHeaders = plugins.fold(headers.toList()) { acc, plugin -> plugin.transformRequestHeaders(endpoint, acc) }

            val httpHeaders = Headers()
            for ((headerName, headerValue) in finalHeaders) {
                httpHeaders.append(headerName, headerValue)
            }

            return httpHeaders
        }

        internal suspend fun send() {
            val response = window.fetch(
                computePath() + computeQuery(),
                RequestInit(
                    method = computeMethod().name,
                    headers = computeHeaders(),
                    body = body
                )
            ).await()

            val status = response.status.toInt()
            val mimeType = response.headers.get("content-type")
            val responseBodyRaw = suspend { Int8Array(response.arrayBuffer().await()).unsafeCast<ByteArray>() }

            val responseContext = ResponseContext(status, response.headers.unsafeCast<Headers>(), responseBodyRaw)

            val apiResponse = endpoint.getResponseByCodeAndMime(status, mimeType)
            if (apiResponse == null) {
                fallbackHandlers.asSequence()
                    .filter { it.first.status == status && it.first.contentType == mimeType }
                    .map { it.second }
                    .forEach { it.invoke(responseContext) }
                return
            }

            val handler = handlers.get(apiResponse)
            // TODO provide api to handle and warn missing handler early if possible and put some toast on failure to handler
            if (handler == null) throw error("No handler provided")


            handler.invoke(endpoint, responseContext)
        }

        suspend fun describe(block: suspend TEndPoint.() -> Unit) {
            endpoint.block()
            // TODO Do postbuild/completeness/eager checks
        }

        suspend fun <TBody> ResponseSpec.WithBody<TBody>.body(ctx: ResponseContext): TBody {
            return decode(ctx.content())
        }

        private val handlers: MutableMap<ResponseSpec, suspend TEndPoint.(ResponseContext) -> Unit> = mutableMapOf()
        private val fallbackHandlers: MutableList<Pair<FallbackMatching, suspend (ResponseContext) -> Unit>> = mutableListOf()

        fun handler(responseSpec: ResponseSpec, block: suspend TEndPoint.(ctx: ResponseContext) -> Unit) {
            require(endpoint.registeredResponses.contains(responseSpec)) { "Given responseSpec is not a response of the current endpoint" }
            if (handlers.put(responseSpec, block) != null) throw error("Handler for this responseSpec is already registered")
        }

        fun fallbackHandler(status: Int? = null, contentType: String? = null, block: suspend (ctx: ResponseContext) -> Unit) {
            fallbackHandlers.add(FallbackMatching(status, contentType) to block)
        }
    }

    suspend fun <TEndpoint : Endpoint> request(
        endpoint: TEndpoint,
        block: suspend ClientRequestBuilder<TEndpoint>.() -> Unit
    ): Performable {
        val builder = ClientRequestBuilder(endpoint)
        builder.block()
        return Performable(builder)
    }

    private data class FallbackMatching(val status: Int?, val contentType: String?)

    class Performable(val builder: ClientRequestBuilder<*>) {
        suspend fun perform() {
            builder.send()
        }
    }
}
