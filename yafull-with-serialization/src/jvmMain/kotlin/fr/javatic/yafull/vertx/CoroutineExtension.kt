package fr.javatic.yafull.vertx

import io.vertx.ext.web.Route
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun Route.handler(
    scope: CoroutineScope,
    fn: suspend (RoutingContext) -> Unit
): Route {
    handler { ctx ->
        scope.launch(ctx.vertx().dispatcher()) {
            try {
                fn(ctx)
            } catch (e: Exception) {
                ctx.fail(e)
            }
        }
    }

    return this
}
