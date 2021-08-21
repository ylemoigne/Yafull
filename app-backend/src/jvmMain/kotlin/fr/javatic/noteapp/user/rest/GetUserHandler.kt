package fr.javatic.noteapp.user.rest

import fr.javatic.noteapp.database.schema.tables.references.USER
import fr.javatic.noteapp.user.User
import fr.javatic.yafull.vertx.database.jooq.Database
import fr.javatic.yafull.vertx.invoke
import fr.javatic.yafull.vertx.web.EndpointHandler
import io.vertx.ext.web.RoutingContext

class GetUserHandler(
    private val database: Database
) : EndpointHandler<GetUserEndpoint> {
    override suspend fun GetUserEndpoint.handle(ctx: RoutingContext) {
        val id = this.id(ctx)

        val user = database.withConnection {
            select(USER.LOGIN, USER.FULLNAME, USER.ROLES).from(USER).where(USER.ID.eq(id))
                .perform()
                .singleOrNull()
                ?.let {
                    User(
                        id,
                        it[USER.LOGIN]!!,
                        it[USER.FULLNAME]!!,
                        it[USER.ROLES]!!.filterNotNull().toSet(),
                    )
                }
        }

        if (user != null) {
            this.reponseUser(ctx) { user }
        } else {
            ctx.response().setStatusCode(404).end()
        }
    }
}
