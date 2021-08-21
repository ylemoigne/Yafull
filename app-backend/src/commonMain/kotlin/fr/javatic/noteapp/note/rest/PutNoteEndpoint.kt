package fr.javatic.noteapp.note.rest

import fr.javatic.noteapp.RestJson
import fr.javatic.noteapp.note.NoteUpdate
import fr.javatic.yafull.rest.Endpoint
import fr.javatic.yafull.rest.HttpMethod
import fr.javatic.yafull.rest.RestPathBuilder

object PutNoteEndpoint : Endpoint(RestJson, tags) {
    override val method: HttpMethod = HttpMethod.PUT
    override fun declarePath(builder: RestPathBuilder.Root): RestPathBuilder =
        builder.segment("notes")

    val note = requestBody(required = true).jsonContent<NoteUpdate>()

    val updated = responseNoContent()
    val created = responseCreated()
}
