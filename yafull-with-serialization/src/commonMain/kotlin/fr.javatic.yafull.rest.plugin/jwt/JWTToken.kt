package fr.javatic.yafull.rest.plugin.jwt

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonObject as KotlinJsonObject

@Serializable
data class JWTToken(
    val sub: String?,
    val iss: String?,
    val aud: String?,
    val name: String?,
    val scope: Set<String>?,
) {
    constructor(token: KotlinJsonObject) : this(
        (token["sub"] as JsonPrimitive?)?.content,
        (token["iss"] as JsonPrimitive?)?.content,
        (token["aud"] as JsonPrimitive?)?.content,
        (token["name"] as JsonPrimitive?)?.content,
        (token["scope"] as JsonPrimitive?)?.content?.split(' ')?.toSet(),
    )
}
