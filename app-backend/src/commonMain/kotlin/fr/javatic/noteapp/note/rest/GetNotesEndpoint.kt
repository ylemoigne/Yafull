package fr.javatic.noteapp.note.rest

import fr.javatic.noteapp.RestJson
import fr.javatic.noteapp.note.Note
import fr.javatic.yafull.rest.Endpoint
import fr.javatic.yafull.rest.HttpMethod
import fr.javatic.yafull.rest.RestPathBuilder

object GetNotesEndpoint : Endpoint(RestJson, tags) {
    override val method: HttpMethod = HttpMethod.GET
    override fun declarePath(builder: RestPathBuilder.Root): RestPathBuilder =
        builder.literal("notes")

    val responseOk = responseOk().withJsonBody<List<Note>>()
}
