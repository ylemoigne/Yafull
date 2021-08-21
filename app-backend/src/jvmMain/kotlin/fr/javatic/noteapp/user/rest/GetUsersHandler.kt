package fr.javatic.noteapp.user.rest

import fr.javatic.noteapp.database.schema.tables.references.USER
import fr.javatic.noteapp.user.User
import fr.javatic.yafull.vertx.database.jooq.Database
import fr.javatic.yafull.vertx.invoke
import fr.javatic.yafull.vertx.web.EndpointHandler
import io.vertx.ext.web.RoutingContext

class GetUsersHandler(
    private val database: Database
) : EndpointHandler<GetUsersEndpoint> {
    override suspend fun GetUsersEndpoint.handle(ctx: RoutingContext) {
        val users = database.withConnection {
            select(USER.ID, USER.LOGIN, USER.FULLNAME, USER.ROLES).from(USER)
                .perform()
                .map {
                    User(
                        it[USER.ID]!!,
                        it[USER.LOGIN]!!,
                        it[USER.FULLNAME]!!,
                        it[USER.ROLES]!!.filterNotNull().toSet(),
                    )
                }
        }

        this.responseUsers(ctx) { users }
    }
}
