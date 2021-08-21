package fr.javatic.noteapp.user.rest

import fr.javatic.noteapp.RestJson
import fr.javatic.noteapp.user.UserUpdate
import fr.javatic.yafull.rest.Endpoint
import fr.javatic.yafull.rest.HttpMethod
import fr.javatic.yafull.rest.RestPathBuilder

object PutUserEndpoint : Endpoint(RestJson, tags) {
    override val method: HttpMethod = HttpMethod.PUT
    override fun declarePath(builder: RestPathBuilder.Root): RestPathBuilder =
        builder.literal("users")

    val user = requestBody(required = true).jsonContent<UserUpdate>()

    val updated = responseNoContent()
    val created = responseCreated()
}
