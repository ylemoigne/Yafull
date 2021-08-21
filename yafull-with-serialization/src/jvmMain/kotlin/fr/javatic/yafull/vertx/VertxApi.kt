package fr.javatic.yafull.vertx

import fr.javatic.yafull.rest.*
import fr.javatic.yafull.vertx.web.EndpointHandler
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerRequest
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.awaitBlocking
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class VertxApi private constructor(
    vertx: Vertx,
    private val scope: CoroutineScope,
    private val jsonConfiguration: JsonConfiguration,
    private val swaggerVersion: String,
    private val defaultProtocol: String,
    private val bindAddress: String = "localhost",
    private val port: Int,
    private val title: String,
    private val description: String? = null,
    private val version: String,
    private val defaultSecurityRequirements: Set<SecurityRequirement>,
    private val plugins: List<VertxApiPlugin>
) {
    private data class RouteKey(val endpoint: Endpoint, val requestBodies: Set<RequestBody>)

    private val endpointRoutes = LinkedHashMap<RouteKey, EndpointRoute<*>>()

    private val router = Router.router(vertx)

    private var atLeastOneEndpointIsMounted = false
    private var apiIsFinalized = false

    private val allDefaultSecurityRequirements get() = (plugins.flatMap { it.defaultSecurityRequirements } + defaultSecurityRequirements).toSet()

    private val securityRequirementHandlers =
        mutableMapOf<SecurityRequirement, MutableList<suspend (RoutingContext) -> Unit>>()

    init {
        plugins.forEach { it.installSecurityHandlers(this) }
        plugins.forEach { it.installHandlers(this) }
    }

    fun securityHandler(requirement: SecurityRequirement, handler: suspend (RoutingContext) -> Unit) {
        require(!apiIsFinalized) { "SecurityRequirement handlers must be added before VertxApi is built (A call to one of `build*` method occurred before route declaration) " }
        if (atLeastOneEndpointIsMounted) {
            endpointRoutes.keys.forEach {
                LOGGER.warn("Handlers previously added for endpoint `${it.endpoint}` will ignore new handler of `$requirement`")
            }
        }
        val securityHandlers = securityRequirementHandlers.getOrPut(requirement) { mutableListOf() }
        securityHandlers.add(handler)
    }

    fun securityHandler(requirements: Set<SecurityRequirement>, handler: suspend (RoutingContext) -> Unit) {
        for (requirement in requirements) {
            securityHandler(requirement, handler)
        }
    }

    fun route(): Route {
        require(!apiIsFinalized) { "Routes must be declared before VertxApi is built (A call to one of `build*` method occurred before route declaration) " }
        return router.route()
    }

    fun route(method: HttpMethod, path: String): Route {
        require(!apiIsFinalized) { "Routes must be declared before VertxApi is built (A call to one of `build*` method occurred before route declaration) " }
        return router.route(method, path)
    }

    fun route(path: String): Route {
        require(!apiIsFinalized) { "Routes must be declared before VertxApi is built (A call to one of `build*` method occurred before route declaration) " }
        return router.route(path)
    }

    fun <TEndpoint : Endpoint> route(
        endpoint: TEndpoint,
        vararg requestBodies: TEndpoint.() -> RequestBody
    ): EndpointRoute<TEndpoint> {
        require(!apiIsFinalized) { "Routes must be declared before VertxApi is built (A call to one of `build*` method occurred before route declaration) " }

        val realRequestBodies = if (requestBodies.isNotEmpty()) {
            requestBodies.map { r -> endpoint.r() }.toSet()
        } else {
            endpoint.registeredRequestBodies
        }

        val endpointRoute = endpointRoutes.getOrPut(RouteKey(endpoint, realRequestBodies)) {
            val path = endpoint.restPathElements.fold(StringBuilder("/")) { sb, el ->
                when (el) {
                    is RestPathElement.Separator -> sb.append('/')
                    is RestPathElement.Literal -> sb.append(el.value)
                    is RestPathElement.Param -> sb.append(':').append(el.restParameter.name)
                }
            }.toString()

            val route = router.route(endpoint.method.asVertx, path)

            if (realRequestBodies.isNotEmpty()) {
                val mimes = mutableSetOf<String>()
                var hasMultipart = false
                for (requestBody in realRequestBodies) {
                    hasMultipart = hasMultipart || (requestBody.mimeType?.startsWith("multipart/") ?: false)
                    mimes.add(requestBody.mimeType ?: "*/*")
                }

                mimes.forEach { route.consumes(it) }

                route.handler { ctx ->
                    ctx.request().isExpectMultipart = hasMultipart
                    ctx.next()
                }

                route.handler(BodyHandler.create())
            }

            val cors = CorsHandler.create()
                .allowedHeaders(endpoint.headerNames)
                .exposedHeaders(endpoint.responseHeaderNames)
            route.handler(cors)

            EndpointRoute(endpoint, route)
        }

        @Suppress("UNCHECKED_CAST")
        return endpointRoute as EndpointRoute<TEndpoint>
    }

    inner class EndpointRoute<TEndpoint : Endpoint>(
        private val endpoint: TEndpoint,
        private val route: Route
    ) {
        fun handler(handler: EndpointHandler<TEndpoint>) = handler(handler::call)
        fun handler(impl: suspend TEndpoint.(ctx: RoutingContext) -> Unit) {
            LOGGER.info("Registering new handler for $endpoint")
            atLeastOneEndpointIsMounted = true

            val securityRequirements = endpoint.securityRequirements + allDefaultSecurityRequirements
            if (securityRequirements.isNotEmpty() && !securityRequirements.contains(null)) {
                for (securityRequirement in securityRequirements) {
                    val handlers =
                        securityRequirementHandlers[securityRequirement]
                            ?: throw Error("No handler defined for security requirement `$securityRequirement`")
                    for (handler in handlers) {
                        route.handler(scope) { ctx ->
                            handler(ctx)
                        }
                    }
                }
            }
            route.handler(scope) { ctx ->
                impl(endpoint, ctx)
            }
        }
    }

    private fun finalize(mountPoint: String = "/") {
        if (!apiIsFinalized) {
            val openapiSpec = OpenapiSpecFactory.createOpenapiSpec(
                jsonConfiguration,
                defaultProtocol,
                bindAddress,
                port,
                mountPoint,
                title,
                description,
                version,
                (plugins.flatMap { it.defaultSecurityRequirements } + defaultSecurityRequirements).toSet(),
                endpointRoutes.keys.map { it.endpoint }
            )

            val prettyJson = Json { prettyPrint = true }
            val openapiSpecAsString = prettyJson.encodeToString(openapiSpec)

            var order = 0
            router.route("/_doc/").order(order++).handler { it.redirect("index.html") }
            router.route("/_doc/index.html").order(order++).handler { ctx ->
                ctx.response().sendFile("swagger/index.html")
            }
            router.route("/_doc/openapi.json").order(order++).handler { ctx ->
                ctx.response().putHeader("content-type", "application/json")
                ctx.response().end(openapiSpecAsString)
            }
            router.route("/_doc/*").order(order).handler(
                StaticHandler.create("META-INF/resources/webjars/swagger-ui-dist/$swaggerVersion/")
                    .setAllowRootFileSystemAccess(false)
                    .setFilesReadOnly(true)
                    .setIndexPage("index.html")
            )
            apiIsFinalized = true
        }
    }

    fun buildAndMountOn(mountPoint: String, router: Router) {
        finalize(mountPoint)
        router.mountSubRouter(mountPoint, this.router)
    }

    fun buildAsHandler(): (HttpServerRequest) -> Unit {
        finalize()
        return { req: HttpServerRequest ->
            router.handle(req)
        }
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(VertxApi::class.java)

        suspend fun create(
            verticle: CoroutineVerticle,
            jsonConfiguration: JsonConfiguration,
            defaultProtocol: String,
            bindAddress: String = "localhost",
            port: Int,
            title: String,
            version: String,
            description: String? = null,
            defaultSecurityRequirements: Set<SecurityRequirement> = emptySet(),
            plugins: Set<VertxApiPlugin?>
        ): VertxApi {
            val swaggerVersion = awaitBlocking {
                val res = "/swagger/version"
                VertxApi::class.java.getResource(res)?.readText() ?: throw Error("Resource `$res` is missing")
            }

            return VertxApi(
                verticle.vertx,
                verticle,
                jsonConfiguration,
                swaggerVersion,
                defaultProtocol,
                bindAddress,
                port,
                title,
                description,
                version,
                defaultSecurityRequirements,
                plugins.filterNotNull()
            )
        }
    }
}
