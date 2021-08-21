package fr.javatic.yafull.rest

import fr.javatic.yafull.rest.plugin.ApiPlugin

interface ClientApiPlugin : ApiPlugin {
    suspend fun transformRequestMethod(endpoint: Endpoint, method: HttpMethod) = method
    suspend fun transformRequestPath(
        endpoint: Endpoint,
        restPathElements: List<RestPathElement>
    ): List<RestPathElement> = restPathElements

    suspend fun transformRequestQuery(endpoint: Endpoint, queryElements: Map<RestParameter.Query<*>, Any?>) =
        queryElements

    suspend fun transformRequestHeaders(endpoint: Endpoint, headers: List<Pair<String, String>>) = headers
}
