package fr.javatic.noteapp.note.rest

import fr.javatic.noteapp.database.schema.tables.references.NOTE
import fr.javatic.yafull.vertx.database.jooq.Database
import fr.javatic.yafull.vertx.invoke
import fr.javatic.yafull.vertx.web.EndpointHandler
import io.vertx.ext.web.RoutingContext

class DeleteNoteHandler(private val database: Database) : EndpointHandler<DeleteNoteEndpoint> {
    override suspend fun DeleteNoteEndpoint.handle(ctx: RoutingContext) {
        val id = this.id(ctx)

        val deleted = database.withTransaction {
            delete(NOTE).where(NOTE.ID.eq(id))
                .perform()
                .rowCount() > 0
        }

        if (deleted) {
            this.responseOk(ctx)
        } else {
            this.responseNotFound(ctx)
        }
    }
}
