package fr.javatic.yafull.rest.plugin.jwt

import fr.javatic.noteapp.RestJson
import fr.javatic.yafull.rest.Endpoint
import fr.javatic.yafull.rest.HttpMethod
import fr.javatic.yafull.rest.RestPathBuilder

object CreateJWTTokenEndpoint : Endpoint(RestJson, setOf("Auth"), setOf(null)) {
    override val method = HttpMethod.GET
    override fun declarePath(builder: RestPathBuilder.Root): RestPathBuilder =
        builder.literal("jwt")

    val login = requestHeader<String>(name = "X-Auth-Identifier", description = "Login")
    val password = requestHeader<String>(name = "X-Auth-Credential", description = "Password")

    val responseOk = responseOk("Authentication succeed")
        .withStringBody("JWT Token")

    val responseAuthenticationFailed = responseUnauthorized("Authentication failed")
        .withStringBody("Failure Explanation")
}

