package fr.javatic.yafull.vertx.cors

import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.handler.CorsHandler

fun CorsConfig.createHandler(): CorsHandler {
    val handler = CorsHandler.create(allowedOriginPattern)
    if (maxAgeSeconds != null) {
        handler.maxAgeSeconds(maxAgeSeconds)
    }
    if (allowCredentials != null) {
        handler.allowCredentials(allowCredentials)
    }
    for (methodName in allowMethods) {
        handler.allowedMethod(HttpMethod(methodName))
    }
    for (headerName in allowHeaders) {
        handler.allowedHeader(headerName)
    }
    for (headerName in exposedHeaders) {
        handler.exposedHeader(headerName)
    }
    return handler
}
