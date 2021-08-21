package fr.javatic.yafull.rest.plugin.jwt

import kotlinx.serialization.Serializable

@Serializable
data class JwtConfig(
    val keyAlias: String,
    val keyAliasPassword: String? = null,
    val audiences: Set<String>,
    val expirationInSeconds: Int?
)
