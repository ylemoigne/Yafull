package fr.javatic.noteapp.note.rest

import fr.javatic.noteapp.database.schema.tables.references.NOTE
import fr.javatic.noteapp.database.schema.tables.references.USER
import fr.javatic.noteapp.note.Note
import fr.javatic.yafull.vertx.database.jooq.Database
import fr.javatic.yafull.vertx.invoke
import fr.javatic.yafull.vertx.web.EndpointHandler
import io.vertx.ext.web.RoutingContext

class GetNoteHandler(
    private val database: Database
) : EndpointHandler<GetNoteEndpoint> {
    override suspend fun GetNoteEndpoint.handle(ctx: RoutingContext) {
        val id = this.id(ctx)

        val note = database.withConnection {
            select(USER.FULLNAME, NOTE.CREATED_AT, NOTE.TITLE, NOTE.CONTENT).from(NOTE)
                .join(USER).on(USER.ID.eq(NOTE.AUTHOR_ID))
                .where(NOTE.ID.eq(id))
                .perform()
                .singleOrNull()
                ?.let {
                    Note(
                        id,
                        it.get(USER.FULLNAME)!!,
                        it.get(NOTE.CREATED_AT)!!,
                        it.get(NOTE.TITLE)!!,
                        it.get(NOTE.CONTENT)!!,
                    )
                }
        }

        if (note == null) {
            this.responseNotFound(ctx)
        } else {
            this.responseOk(ctx) { note }
        }
    }
}
