package fr.javatic.yafull.rest.plugin.jwt

import fr.javatic.noteapp.config.security.getCertPublicKeyAsPEM
import fr.javatic.noteapp.config.security.getPrivateKeyAlgorithm
import fr.javatic.noteapp.config.security.getPrivateKeyAsPEM
import fr.javatic.yafull.utils.password.PasswordChecker
import fr.javatic.yafull.vertx.VertxApi
import fr.javatic.yafull.vertx.VertxApiPlugin
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.JWTAuthHandler
import io.vertx.kotlin.ext.auth.jwt.jwtAuthOptionsOf
import io.vertx.kotlin.ext.auth.jwtOptionsOf
import io.vertx.kotlin.ext.auth.pubSecKeyOptionsOf
import java.security.KeyStore

class JWTVertxApiPlugin(
    val vertx: Vertx,
    keystore: KeyStore,
    private val passwordChecker: PasswordChecker,
    private val config: JwtConfig,
    private val authenticationUserInfoRetrieve: suspend (identifier: String) -> CreateJWTTokenHandler.AuthenticationUserInfo?
) : JWTApiPlugin(), VertxApiPlugin {
    val jwtAuth: JWTAuth by lazy {
        // See io.vertx.ext.auth.impl.jose.JWK.<init>(JWK.java:226)
        val vertxJwtKeyAlgorithm =
            when (keystore.getPrivateKeyAlgorithm(config.keyAlias, config.keyAliasPassword?.toCharArray())) {
                "RSA" -> "RS256" // Whatever the size, it will be ignored...
                "EC" -> "ES256" // Whatever the size, it will be ignored...
                "EdDSA" -> "EdDSA"
                else -> throw Error("Unmanaged")
            }

        JWTAuth.create(
            vertx,
            jwtAuthOptionsOf(
                jwtOptions = jwtOptionsOf(
                    audiences = config.audiences,
                    expiresInSeconds = config.expirationInSeconds,
                ),
                pubSecKeys = listOf(
                    pubSecKeyOptionsOf(
                        algorithm = vertxJwtKeyAlgorithm,
                        buffer = Buffer.buffer(
                            keystore.getPrivateKeyAsPEM(
                                config.keyAlias,
                                config.keyAliasPassword?.toCharArray()
                            )
                        )
                    ),
                    pubSecKeyOptionsOf(
                        algorithm = vertxJwtKeyAlgorithm,
                        buffer = Buffer.buffer(
                            keystore.getCertPublicKeyAsPEM(
                                config.keyAlias
                            )
                        )
                    )
                )
            )
        )
    }

    private val vertxJwtAuthHandler = JWTAuthHandler.create(jwtAuth)

    override fun installSecurityHandlers(api: VertxApi) {
        api.securityHandler(baseSecurityRequirement) { ctx ->
            vertxJwtAuthHandler.handle(ctx)
        }
        api.securityHandler(baseSecurityRequirement) { ctx ->
            val principal = ctx.user().principal()
            ctx.jwtToken = JWTToken(
                principal.getString("sub"),
                principal.getString("iss"),
                principal.getString("aud"),
                principal.getString("name"),
                principal.getString("scope")?.split(" ")?.toSet(),
            )
            ctx.next()
        }
    }

    override fun installHandlers(api: VertxApi) {
        api.route(CreateJWTTokenEndpoint).handler(
            CreateJWTTokenHandler(
                jwtAuth,
                config,
                passwordChecker,
                authenticationUserInfoRetrieve
            )
        )
    }

    companion object {
        private val token_key = JWTVertxApiPlugin::class.java.name + "/token"

        var RoutingContext.jwtToken: JWTToken?
            get() = this.get(token_key)
            private set(value) {
                this.put(token_key, value)
            }
    }
}
