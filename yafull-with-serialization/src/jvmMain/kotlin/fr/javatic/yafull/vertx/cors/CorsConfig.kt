package fr.javatic.yafull.vertx.cors

import kotlinx.serialization.Serializable

@Serializable
data class CorsConfig(
    val allowedOriginPattern: String,
    val allowMethods: Set<String> = emptySet(),
    val allowHeaders: Set<String> = emptySet(),
    val exposedHeaders: Set<String> = emptySet(),
    val maxAgeSeconds: Int? = null,
    val allowCredentials: Boolean? = null
)
