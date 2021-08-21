package fr.javatic.noteapp

import fr.javatic.noteapp.config.security.getCertAsPEM
import fr.javatic.noteapp.config.security.getPrivateKeyAsPEM
import fr.javatic.noteapp.database.schema.tables.references.USER
import fr.javatic.noteapp.note.rest.*
import fr.javatic.noteapp.user.rest.*
import fr.javatic.yafull.rest.plugin.jwt.CreateJWTTokenHandler
import fr.javatic.yafull.rest.plugin.jwt.JWTVertxApiPlugin
import fr.javatic.yafull.utils.password.PasswordChecker
import fr.javatic.yafull.utils.password.PasswordCheckerConfig
import fr.javatic.yafull.utils.password.PasswordHasher
import fr.javatic.yafull.utils.password.PasswordStrengthEstimator
import fr.javatic.yafull.vertx.VertxApi
import fr.javatic.yafull.vertx.cors.createHandler
import fr.javatic.yafull.vertx.database.jooq.Database
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.ErrorHandler
import io.vertx.ext.web.handler.LoggerFormat
import io.vertx.ext.web.handler.LoggerHandler
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.kotlin.core.http.httpServerOptionsOf
import io.vertx.kotlin.core.json.jsonObjectOf
import io.vertx.kotlin.core.net.pemKeyCertOptionsOf
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.security.KeyStore

class WebServerVerticle(
    private val devMode: Boolean,
    private val webConfig: AppConfig.WebConfig,
    private val passwordHasher: PasswordHasher,
    private val passwordStrengthEstimator: PasswordStrengthEstimator,
    private val passwordCheckerConfig: PasswordCheckerConfig,
    private val database: Database,
    private val keystore: KeyStore,
) : CoroutineVerticle() {
    override suspend fun start() {
        val passwordChecker = PasswordChecker(
            passwordCheckerConfig.maxAttemptBeforeLock,
            passwordCheckerConfig.lockoutDurationInSeconds,
            passwordHasher
        )

        val jwtVertxPlugin = JWTVertxApiPlugin(vertx, keystore, passwordChecker, webConfig.jwt) { identifier ->
            database.withConnection {
                select(USER.ID, USER.FULLNAME, USER.ROLES, USER.HASHED_PASSWORD).from(USER).where(USER.LOGIN.eq(identifier))
                    .perform()
                    .singleOrNull()
                    ?.let { row ->
                        val userId = row[USER.ID]!!
                        val fullname = row[USER.FULLNAME]!!
                        val roles = row[USER.ROLES]!!.filterNotNull()
                        val hashedPassword = row[USER.HASHED_PASSWORD]!!
                        CreateJWTTokenHandler.AuthenticationUserInfo(
                            hashedPassword,
                            jsonObjectOf(
                                "sub" to userId.value,
                                "name" to fullname,
                                "scope" to roles.joinToString(" ")
                            )
                        )
                    }
            }
        }

        val router = Router.router(vertx)
        router.route().failureHandler(ErrorHandler.create(vertx, devMode))
        router.route().handler(LoggerHandler.create(LoggerFormat.TINY))
        router.route().handler(
            StaticHandler.create("META-INF/frontend/")
                .setAllowRootFileSystemAccess(false)
                .setFilesReadOnly(true)
                .setIndexPage("index.html")
        )

        if (webConfig.cors != null) {
            router.route().handler(webConfig.cors.createHandler())
        }

        val bindAddress = "localhost"
        createAppApi(
            bindAddress,
            jwtVertxPlugin,
            database,
            passwordStrengthEstimator,
            passwordHasher
        ).buildAndMountOn("/api", router)

        coroutineScope {
            launch {
                val server = vertx.createHttpServer()
                    .requestHandler(router)
                    .listen(webConfig.port)
                    .await()
                LOGGER.info("HTTP Server available at http://$bindAddress:${server.actualPort()}")
            }

            if (webConfig.https != null) {
                launch {
                    val server = vertx.createHttpServer(
                        httpServerOptionsOf(
                            ssl = true,
                            pemKeyCertOptions = pemKeyCertOptionsOf(
                                keyValue = Buffer.buffer(
                                    keystore.getPrivateKeyAsPEM(
                                        webConfig.https.certAlias,
                                        webConfig.https.certAliasPassword?.toCharArray()
                                    )
                                ),
                                certValue = Buffer.buffer(
                                    keystore.getCertAsPEM(webConfig.https.certAlias)
                                ),
                            )
                        )
                    )
                        .requestHandler(router)
                        .listen(webConfig.https.port)
                        .await()
                    LOGGER.info("HTTPS Server available at https://$bindAddress:${server.actualPort()}")
                }
            }
        }
    }

    private suspend fun createAppApi(
        bindAddress: String,
        jwtVertxPlugin: JWTVertxApiPlugin,
        database: Database,
        passwordStrengthEstimator: PasswordStrengthEstimator,
        passwordHasher: PasswordHasher
    ): VertxApi {
        val api = VertxApi.create(
            this,
            RestJson.configuration,
            "http",
            bindAddress,
            webConfig.port,
            "The Yafull Notes App",
            "v1.0",
            plugins = setOf(
                jwtVertxPlugin
            )
        )
        api.route(GetNoteEndpoint).handler(GetNoteHandler(database))
        api.route(GetNotesEndpoint).handler(GetNotesHandler(database))
        api.route(PutNoteEndpoint).handler(PutNoteHandler(database))
        api.route(DeleteNoteEndpoint).handler(DeleteNoteHandler(database))
        api.route(LoginIsAvailableEndpoint).handler(LoginIsAvailableHandler(database))
        api.route(EstimatePasswordStrengthEndpoint).handler(EstimatePasswordStrengthHandler(passwordStrengthEstimator))
        api.route(GetCurrentUserEndpoint).handler(GetCurrentUserHandler(database))
        api.route(GetUserEndpoint).handler(GetUserHandler(database))
        api.route(GetUsersEndpoint).handler(GetUsersHandler(database))
        api.route(PutUserEndpoint).handler(PutUserHandler(database, passwordHasher))
        api.route(DeleteUserEndpoint).handler(DeleteUserHandler(database))

        return api
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(WebServerVerticle::class.java)
    }
}
