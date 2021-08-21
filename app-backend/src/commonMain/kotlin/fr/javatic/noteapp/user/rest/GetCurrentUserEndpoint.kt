package fr.javatic.noteapp.user.rest

import fr.javatic.noteapp.RestJson
import fr.javatic.noteapp.user.User
import fr.javatic.yafull.rest.Endpoint
import fr.javatic.yafull.rest.HttpMethod
import fr.javatic.yafull.rest.RestPathBuilder

object GetCurrentUserEndpoint : Endpoint(RestJson, tags) {
    override val method: HttpMethod = HttpMethod.GET
    override fun declarePath(builder: RestPathBuilder.Root): RestPathBuilder =
        builder.segment("users").segment("current")

    val reponseUser = responseOk().withJsonBody<User>()
}
