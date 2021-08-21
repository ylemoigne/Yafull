package fr.javatic.noteapp.note.rest

import fr.javatic.noteapp.RestJson
import fr.javatic.noteapp.note.Note
import fr.javatic.util.UUIDv4
import fr.javatic.yafull.rest.Endpoint
import fr.javatic.yafull.rest.HttpMethod
import fr.javatic.yafull.rest.RestPathBuilder

object GetNoteEndpoint : Endpoint(RestJson, tags) {
    override val method: HttpMethod = HttpMethod.GET
    override fun declarePath(builder: RestPathBuilder.Root): RestPathBuilder =
        builder.segment("notes").parameter(id)

    val id = path<UUIDv4>("id")

    val responseOk = responseOk().withJsonBody<Note>()
    val responseNotFound = responseNotFound("No Note with this id")
}
