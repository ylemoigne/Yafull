package fr.javatic.noteapp.note.rest

import fr.javatic.noteapp.database.schema.tables.references.NOTE
import fr.javatic.noteapp.note.Note
import fr.javatic.util.UUIDv4
import fr.javatic.yafull.vertx.database.jooq.Database
import fr.javatic.yafull.vertx.invoke
import fr.javatic.yafull.vertx.web.EndpointHandler
import io.vertx.ext.web.RoutingContext

class GetNotesHandler(
    private val database: Database
) : EndpointHandler<GetNotesEndpoint> {
    override suspend fun GetNotesEndpoint.handle(ctx: RoutingContext) {
        val fullName = ctx.user().principal().getString("name")
        val userId = ctx.user().principal().getString("sub")

        val notes = database.withConnection {
            select(NOTE.ID, NOTE.CREATED_AT, NOTE.TITLE, NOTE.CONTENT).from(NOTE)
                .where(NOTE.AUTHOR_ID.eq(UUIDv4(userId)))
                .perform()
                .map {
                    Note(
                        it.get(NOTE.ID)!!,
                        fullName,
                        it.get(NOTE.CREATED_AT)!!,
                        it.get(NOTE.TITLE)!!,
                        it.get(NOTE.CONTENT)!!,
                    )
                }
        }

        this.responseOk(ctx) { notes }
    }
}
