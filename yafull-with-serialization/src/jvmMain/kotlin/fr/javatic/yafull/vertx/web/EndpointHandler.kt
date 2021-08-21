package fr.javatic.yafull.vertx.web

import fr.javatic.yafull.rest.Endpoint
import io.vertx.ext.web.RoutingContext

interface EndpointHandler<TEndpoint : Endpoint> {
    suspend fun call(endpoint: TEndpoint, ctx: RoutingContext) {
        with(endpoint) { handle(ctx) }
    }

    suspend fun TEndpoint.handle(ctx: RoutingContext)
}
