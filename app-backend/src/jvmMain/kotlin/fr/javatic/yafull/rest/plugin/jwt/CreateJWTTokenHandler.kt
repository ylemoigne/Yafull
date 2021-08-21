package fr.javatic.yafull.rest.plugin.jwt

import fr.javatic.yafull.utils.password.PasswordChecker
import fr.javatic.yafull.vertx.invoke
import fr.javatic.yafull.vertx.web.EndpointHandler
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.awaitBlocking
import io.vertx.kotlin.ext.auth.jwtOptionsOf

class CreateJWTTokenHandler(
    private val jwtProvider: JWTAuth,
    private val config: JwtConfig,
    private val passwordChecker: PasswordChecker,
    private val authenticationUserInfoRetrieve: suspend (identifier: String) -> AuthenticationUserInfo?
) : EndpointHandler<CreateJWTTokenEndpoint> {
    data class AuthenticationUserInfo(val hashedPassword: String, val claims: JsonObject? = null)

    override suspend fun CreateJWTTokenEndpoint.handle(ctx: RoutingContext) {
        val identifier = login(ctx)
        val password = password(ctx)

        val user = authenticationUserInfoRetrieve(identifier)

        when (
            val res = awaitBlocking { passwordChecker.check(identifier, password, user?.hashedPassword) }) {
            is PasswordChecker.AuthResult.Ok -> {
                val token = jwtProvider.generateToken(
                    user?.claims ?: JsonObject(),
                    jwtOptionsOf(
                        subject = user?.claims?.getString("sub") ?: identifier,
                        algorithm = "RS256",
                        audiences = config.audiences,
                        expiresInSeconds = config.expirationInSeconds
                    )
                )

                this.responseOk(ctx) { token }
            }
            is PasswordChecker.AuthResult.Unauthorized -> this.responseAuthenticationFailed(ctx) { res.message }
        }
    }
}

