package fr.javatic.noteapp.note.rest

import fr.javatic.noteapp.database.schema.tables.references.NOTE
import fr.javatic.util.UUIDv4
import fr.javatic.yafull.rest.plugin.jwt.JWTVertxApiPlugin.Companion.jwtToken
import fr.javatic.yafull.vertx.database.jooq.Database
import fr.javatic.yafull.vertx.database.jooq.DatabaseOperation
import fr.javatic.yafull.vertx.invoke
import fr.javatic.yafull.vertx.web.EndpointHandler
import io.vertx.ext.web.RoutingContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class PutNoteHandler(private val database: Database) : EndpointHandler<PutNoteEndpoint> {
    override suspend fun PutNoteEndpoint.handle(ctx: RoutingContext) {
        val note = this.note(ctx)
        val userId = ctx.jwtToken?.sub ?: throw error("User must be identified")

        val upsertResult = database.withTransaction {
            insertInto(NOTE)
                .set(NOTE.ID, note.id)
                .set(NOTE.AUTHOR_ID, UUIDv4(userId))
                .set(NOTE.CREATED_AT, Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()))
                .set(NOTE.TITLE, note.title)
                .set(NOTE.CONTENT, note.content)
                .onDuplicateKeyUpdate()
                .set(NOTE.TITLE, note.title)
                .set(NOTE.CONTENT, note.content)
                .upsert()
        }

        when (upsertResult) {
            DatabaseOperation.UpsertResult.INSERT -> this.created(ctx)
            DatabaseOperation.UpsertResult.UPDATE -> this.updated(ctx)
        }
    }
}
