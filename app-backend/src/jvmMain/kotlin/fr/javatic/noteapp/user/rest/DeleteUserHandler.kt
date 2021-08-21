package fr.javatic.noteapp.user.rest

import fr.javatic.noteapp.database.schema.tables.references.USER
import fr.javatic.yafull.vertx.database.jooq.Database
import fr.javatic.yafull.vertx.invoke
import fr.javatic.yafull.vertx.web.EndpointHandler
import io.vertx.ext.web.RoutingContext

class DeleteUserHandler(private val database: Database) : EndpointHandler<DeleteUserEndpoint> {
    override suspend fun DeleteUserEndpoint.handle(ctx: RoutingContext) {
        val id = id(ctx)

        val deleted = database.withTransaction {
            delete(USER).where(USER.ID.eq(id))
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
