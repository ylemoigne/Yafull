package fr.javatic.noteapp.user.rest

import fr.javatic.noteapp.RestJson
import fr.javatic.yafull.rest.Endpoint
import fr.javatic.yafull.rest.HttpMethod
import fr.javatic.yafull.rest.RestPathBuilder

object LoginIsAvailableEndpoint : Endpoint(RestJson, tags) {
    override val method: HttpMethod = HttpMethod.GET
    override fun declarePath(builder: RestPathBuilder.Root): RestPathBuilder =
        builder.segment("users").literal("login-is-available")

    val login = requestQuery<String>("login")

    val responseIsAvailable = responseOk().withJsonBody<Boolean>()
}
