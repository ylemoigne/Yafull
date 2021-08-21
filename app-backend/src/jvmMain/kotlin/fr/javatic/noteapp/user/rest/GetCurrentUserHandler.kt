package fr.javatic.noteapp.user.rest

import fr.javatic.noteapp.database.schema.tables.references.USER
import fr.javatic.noteapp.user.User
import fr.javatic.util.UUIDv4
import fr.javatic.yafull.rest.plugin.jwt.JWTVertxApiPlugin.Companion.jwtToken
import fr.javatic.yafull.vertx.database.jooq.Database
import fr.javatic.yafull.vertx.invoke
import fr.javatic.yafull.vertx.web.EndpointHandler
import io.vertx.ext.web.RoutingContext

class GetCurrentUserHandler(
    private val database: Database
) : EndpointHandler<GetCurrentUserEndpoint> {
    override suspend fun GetCurrentUserEndpoint.handle(ctx: RoutingContext) {
        val id = ctx.jwtToken?.sub?.let { UUIDv4(it) } ?: throw error("User must be identitied")

        val user = database.withConnection {
            select(USER.LOGIN, USER.FULLNAME, USER.ROLES).from(USER).where(USER.ID.eq(id)).perform().singleOrNull()
                ?.let { User(id, it[USER.LOGIN]!!, it[USER.FULLNAME]!!, it[USER.ROLES]!!.filterNotNull().toSet()) }
        }
            ?: throw error("If user is identified, it should exist (In fact it's possible that no, if the user is logged, then was deleted, it got a valid token which do not match a user anymore. Also, if server is restarted on empty db, the admin user recreated with a new id. But for the demo let assume if we got here, eveything it fine)")

        this.reponseUser(ctx) { user }
    }
}
