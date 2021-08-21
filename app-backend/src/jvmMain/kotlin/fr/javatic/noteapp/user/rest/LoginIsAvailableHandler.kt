package fr.javatic.noteapp.user.rest

import fr.javatic.noteapp.database.schema.tables.references.USER
import fr.javatic.yafull.vertx.database.jooq.Database
import fr.javatic.yafull.vertx.invoke
import fr.javatic.yafull.vertx.web.EndpointHandler
import io.vertx.ext.web.RoutingContext
import org.jooq.impl.DSL.*

class LoginIsAvailableHandler(
    private val database: Database
) : EndpointHandler<LoginIsAvailableEndpoint> {
    override suspend fun LoginIsAvailableEndpoint.handle(ctx: RoutingContext) {
        val login = this.login(ctx)
        val exists = database.withConnection {
            select(field(exists(select(value(1)).from(USER).where(USER.LOGIN.eq(login)))))
                .perform()
                .single()
                .getBoolean(0)
        }

        this.responseIsAvailable(ctx) { !exists }
    }
}
