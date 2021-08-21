package fr.javatic.yafull.vertx.web

import io.vertx.ext.web.RoutingContext

fun interface CoroutineWebHandler : CoroutineHandler<RoutingContext> {
    override suspend fun handle(@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") ctx: RoutingContext)
}
