package fr.javatic.yafull.vertx

import fr.javatic.yafull.rest.plugin.ApiPlugin

interface VertxApiPlugin : ApiPlugin {
    fun installSecurityHandlers(api: VertxApi)
    fun installHandlers(api: VertxApi)
}
