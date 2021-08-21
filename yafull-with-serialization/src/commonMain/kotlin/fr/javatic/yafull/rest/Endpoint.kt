package fr.javatic.yafull.rest

import kotlinx.serialization.json.*
import kotlin.reflect.KType
import kotlin.reflect.typeOf

abstract class Endpoint(
    private val json: Json,
    private val tags: Set<String> = emptySet(),
    val securityRequirements: Set<SecurityRequirement?> = emptySet()
) {
    private val parametersPath = mutableMapOf<String, RestParameter.Path<*>>()
    private val parametersQuery = mutableMapOf<String, RestParameter.Query<*>>()
    private val parametersHeader = mutableMapOf<String, RestParameter.Header<*>>()
    private val responseHeaders = mutableListOf<ResponseHeader<*>>()

    private var requestBody: RequestBodyUntyped? = null
    internal val registeredRequestBodies: Set<RequestBody>
        get() = requestBody?.let {
            if (it.requestByMime.isEmpty()) {
                setOf(it)
            } else {
                it.requestByMime.values.toSet()
            }
        } ?: emptySet()

    abstract val method: HttpMethod
    abstract fun declarePath(builder: RestPathBuilder.Root): RestPathBuilder

    private val responses = mutableMapOf<Int, ResponseSpec.Base>()

    internal val restPathElements get() = declarePath(RestPathBuilder.create()).build()
    internal val registeredResponses
        get() = buildList<ResponseSpec> {
            for (baseResponse in responses.values) {
                if (baseResponse.responses.isEmpty()) {
                    add(baseResponse)
                } else {
                    for (bodyResponse in baseResponse.responses.values) {
                        add(bodyResponse)
                    }
                }
            }
        }

    internal val headerNames get() = parametersHeader.values.map { it.name }.toSet()
    internal val responseHeaderNames get() = responseHeaders.map { it.name }.toSet()

    internal fun getResponseByCodeAndMime(status: Int, mimeType: String?): ResponseSpec? {
        val responseSpecBase = responses[status]
        if (responseSpecBase == null || mimeType == null) return responseSpecBase
        return responseSpecBase.responses[mimeType] ?: responseSpecBase
    }

    internal fun createOpenapiOperationData(schemasInfoHolder: OpenapiSpecFactory.SchemasInfoHolder): OpenapiOperationData {
        val path = restPathElements.fold(StringBuilder("/")) { sb, el ->
            when (el) {
                is RestPathElement.Separator -> sb.append('/')
                is RestPathElement.Literal -> sb.append(el.value)
                is RestPathElement.Param -> sb.append('{').append(el.restParameter.name).append('}')
                //is RestPathElement.Wildcard -> sb.append('*')
            }
        }.toString()

        val reqBody = requestBody

        return OpenapiOperationData(
            path,
            method.name.lowercase(),
            tags,
            securityRequirements,
            buildJsonObject {
                put("tags", JsonArray(tags.map { JsonPrimitive(it) }))
                if (securityRequirements.isNotEmpty()) {
                    put("security", buildJsonArray {
                        for (security in securityRequirements) {
                            add(
                                if (security == null) JsonObject(emptyMap())
                                else buildJsonObject {
                                    put(security.scheme.name, buildJsonArray {
                                        security.scopes.forEach { add(it) }
                                    })
                                }
                            )
                        }
                    })
                }
                if (parametersPath.isNotEmpty() || parametersQuery.isNotEmpty() || parametersHeader.isNotEmpty()) {
                    putJsonArray("parameters") {
                        for (param in parametersPath.values) {
                            add(param.createOpenapi(schemasInfoHolder))
                        }
                        for (param in parametersQuery.values) {
                            add(param.createOpenapi(schemasInfoHolder))
                        }
                        for (param in parametersHeader.values) {
                            add(param.createOpenapi(schemasInfoHolder))
                        }
                    }
                }
                if (reqBody != null) {
                    put("requestBody", reqBody.createOpenapi(schemasInfoHolder))
                }
                putJsonObject("responses") {
                    for (resp in registeredResponses) {
                        put(resp.status.toString(), resp.createOpenapi(schemasInfoHolder))
                    }
                }
            })
    }

    protected inline fun <reified T> path(
        name: String,
        description: String? = null,
        format: ParameterFormat.Path = ParameterFormat.Path.Simple
    ) = registerPathParameter<T>(typeOf<T>(), name, description, format)

    protected fun <T> registerPathParameter(
        typeOf: KType,
        name: String,
        description: String?,
        format: ParameterFormat.Path
    ): RestParameter.Path<T> {
        val restParameterPath: RestParameter.Path<T> = RestParameter.Path(typeOf, name, description, format)
        if (parametersPath.put(
                name,
                restParameterPath
            ) != null
        ) throw Error("Path parameter with name `$name` is already registered")
        return restParameterPath
    }

    protected inline fun <reified T> requestQuery(
        name: String,
        description: String? = null,
        format: ParameterFormat.Query = ParameterFormat.Query.Form()
    ) = registerQueryParameter<T>(typeOf<T>(), name, description, format)

    protected fun <T> registerQueryParameter(
        typeOf: KType,
        name: String,
        description: String?,
        format: ParameterFormat.Query
    ): RestParameter.Query<T> {
        val restParameterQuery: RestParameter.Query<T> = RestParameter.Query(typeOf, name, description, format)
        if (parametersQuery.put(
                name,
                restParameterQuery
            ) != null
        ) throw Error("Path parameter with name `$name` is already registered")
        return restParameterQuery
    }

    protected inline fun <reified T> requestHeader(
        name: String,
        description: String? = null,
        format: ParameterFormat.Header = ParameterFormat.Header.Simple
    ) = registerHeaderParameter<T>(typeOf<T>(), name, description, format)

    protected fun <T> registerHeaderParameter(
        typeOf: KType,
        name: String,
        description: String?,
        format: ParameterFormat.Header
    ): RestParameter.Header<T> {
        val restParameterHeader: RestParameter.Header<T> = RestParameter.Header(typeOf, name, description, format)
        if (parametersHeader.put(
                name,
                restParameterHeader
            ) != null
        ) throw Error("Path parameter with name `$name` is already registered")
        return restParameterHeader
    }

    protected fun requestBody(description: String? = null, required: Boolean = false): RequestBodyUntyped {
        if (requestBody != null) throw Error("Request body is already registered")
        val ret = RequestBodyUntyped(description, required, json)
        requestBody = ret
        return ret
    }

    protected inline fun <reified T> responseHeader(
        name: String,
        description: String? = null,
        format: ParameterFormat.Header = ParameterFormat.Header.Simple
    ) = registerResponseHeaderParameter<T>(typeOf<T>(), name, description, format)

    protected fun <T> registerResponseHeaderParameter(
        typeOf: KType,
        name: String,
        description: String?,
        format: ParameterFormat.Header
    ): ResponseHeader<T> {
        val responseHeader = ResponseHeader<T>(typeOf, name, description, format)
        responseHeaders.add(responseHeader)
        return responseHeader
    }

    protected fun responseCustom(
        status: Int,
        statusMessage: String? = null,
        responseDescription: String = "No Description",
        responseHeaders: Array<out ResponseHeader<*>> = emptyArray()
    ): ResponseSpec.Base {
        if (responseHeaders.groupBy { it.name }.values.any { it.size > 1 }) throw Error("Multiple header with same name declared")

        val baseSpec = ResponseSpec.Base(
            status,
            statusMessage,
            responseDescription,
            responseHeaders.toSet(),
            json
        )
        if (responses.put(
                status,
                baseSpec
            ) != null
        ) throw Error("Response with status code `$status` is already declared")

        return baseSpec
    }


    protected fun responseOk(
        responseDescription: String = "Success",
        vararg responseHeaders: ResponseHeader<*> = emptyArray(),
    ) = responseCustom(200, null, responseDescription, responseHeaders)

    protected fun responseCreated(
        responseDescription: String = "Success",
        vararg responseHeaders: ResponseHeader<*> = emptyArray(),
    ) = responseCustom(201, null, responseDescription, responseHeaders)

    protected fun responseAccepted(
        responseDescription: String = "Success",
        vararg responseHeaders: ResponseHeader<*> = emptyArray(),
    ) = responseCustom(202, null, responseDescription, responseHeaders)

    protected fun responseNoContent(
        responseDescription: String = "Success",
        vararg responseHeaders: ResponseHeader<*> = emptyArray()
    ) = responseCustom(202, null, responseDescription, responseHeaders)

    protected fun responseBadRequest(
        responseDescription: String = "Client Error",
        vararg responseHeaders: ResponseHeader<*> = emptyArray(),
    ) = responseCustom(400, null, responseDescription, responseHeaders)

    protected fun responseUnauthorized(
        responseDescription: String = "Client Error",
        vararg responseHeaders: ResponseHeader<*> = emptyArray(),
    ) = responseCustom(401, null, responseDescription, responseHeaders)

    protected fun responseForbidden(
        responseDescription: String = "Client Error",
        vararg responseHeaders: ResponseHeader<*> = emptyArray(),
    ) = responseCustom(403, null, responseDescription, responseHeaders)

    protected fun responseNotFound(
        responseDescription: String = "Client Error",
        vararg responseHeaders: ResponseHeader<*> = emptyArray(),
    ) = responseCustom(404, null, responseDescription, responseHeaders)

    protected fun responseConflict(
        responseDescription: String = "Client Error",
        vararg responseHeaders: ResponseHeader<*> = emptyArray(),
    ) = responseCustom(409, null, responseDescription, responseHeaders)

    override fun toString(): String {
        return "${this::class.simpleName}(method=$method, path=${restPathElements.joinToString("")})"
    }
}

