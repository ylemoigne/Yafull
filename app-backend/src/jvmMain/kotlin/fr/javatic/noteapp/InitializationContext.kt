package fr.javatic.noteapp

import fr.javatic.noteapp.database.DatabaseInitializer
import fr.javatic.yafull.utils.password.PasswordHasher
import fr.javatic.yafull.utils.password.PasswordStrengthEstimator
import fr.javatic.yafull.vertx.database.jooq.Database
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Path
import java.security.KeyStore
import kotlin.io.path.readText

class InitializationContext(
    val vertx: Vertx,
    val appConfig: AppConfig,
    val passwordHasher: PasswordHasher,
    val passwordStrengthEstimator: PasswordStrengthEstimator,
    val database: Database,
    val keystore: KeyStore,
) {
    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(InitializationContext::class.java)

        suspend fun initialize(configurationFile: Path): InitializationContext {
            val argsDisplay = CLI.args.joinToString(", ") { "\"$it\"" }
            LOGGER.info("Initialization (cwd:${System.getProperty("user.dir")}, args:$argsDisplay)")

            LOGGER.info("Load configuration from $configurationFile")
            val configDeserializer = Json { classDiscriminator = "@type" }
            val appConfig = configDeserializer.decodeFromString<AppConfig>(configurationFile.readText())
            System.setProperty("vertxweb.environment", if (appConfig.dev) "DEV" else "")


            LOGGER.info(("Load keystore"))
            val keystore = KeyStore.getInstance(
                File(appConfig.keystore.path),
                appConfig.keystore.password?.toCharArray()
            )

            LOGGER.info("Initialize Password Hasher")
            val passwordHasher = PasswordHasher(appConfig.password.hasher)

            LOGGER.info("Initialize Password Validator")
            val passwordStrengthEstimator =
                PasswordStrengthEstimator(passwordHasher, appConfig.password.strengthEstimator)

            LOGGER.info("Initialize vertx")
            val vertx = Vertx.vertx()
            vertx.exceptionHandler { LOGGER.error("Uncatched exception", it) }

            LOGGER.info("Initialize database")
            val initializer = DatabaseInitializer(vertx, appConfig.database)
            withContext(vertx.dispatcher()) {
                initializer.initialize(
                    passwordStrengthEstimator,
                    passwordHasher,
                    appConfig.password.defaultAdminPassword
                )
            }

            return InitializationContext(
                vertx,
                appConfig,
                passwordHasher,
                passwordStrengthEstimator,
                initializer.database,
                keystore
            )
        }
    }
}
