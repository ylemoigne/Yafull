package fr.javatic.noteapp.user.rest

import fr.javatic.noteapp.database.schema.tables.references.USER
import fr.javatic.yafull.utils.password.PasswordHasher
import fr.javatic.yafull.vertx.database.jooq.Database
import fr.javatic.yafull.vertx.database.jooq.DatabaseOperation
import fr.javatic.yafull.vertx.invoke
import fr.javatic.yafull.vertx.web.EndpointHandler
import io.vertx.ext.web.RoutingContext

class PutUserHandler(private val database: Database, private val passwordHasher: PasswordHasher) :
    EndpointHandler<PutUserEndpoint> {
    override suspend fun PutUserEndpoint.handle(ctx: RoutingContext) {
        val user = this.user(ctx)

        val id = user.id
        val login = user.login
        val roles: Array<String?> = user.roles.toTypedArray()
        val fullName = user.fullname
        val hashedPassword = user.password?.let { passwordHasher.hash(it.toCharArray()) }

        val upsertResult = if (hashedPassword == null) {
            val count = database.withTransaction {
                update(USER)
                    .set(USER.LOGIN, login)
                    .set(USER.ROLES, roles)
                    .set(USER.FULLNAME, fullName)
                    .where(USER.ID.eq(id))
                    .perform()
                    .rowCount()
            }
            if (count == 0) throw error("Hashed password is mandatory for new user")

            DatabaseOperation.UpsertResult.UPDATE
        } else {
            database.withTransaction {
                insertInto(USER)
                    .set(USER.ID, id)
                    .set(USER.LOGIN, login)
                    .set(USER.ROLES, roles)
                    .set(USER.FULLNAME, fullName)
                    .set(USER.HASHED_PASSWORD, hashedPassword)
                    .onDuplicateKeyUpdate()
                    .set(USER.LOGIN, login)
                    .set(USER.ROLES, roles)
                    .set(USER.FULLNAME, fullName)
                    .set(USER.HASHED_PASSWORD, hashedPassword)
                    .upsert()
            }
        }

        when (upsertResult) {
            DatabaseOperation.UpsertResult.UPDATE -> this.updated.invoke(ctx)
            DatabaseOperation.UpsertResult.INSERT -> this.created(ctx)
        }
    }
}
