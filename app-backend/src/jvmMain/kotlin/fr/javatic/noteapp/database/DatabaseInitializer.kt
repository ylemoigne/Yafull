package fr.javatic.noteapp.database

// Don't like that, but : https://youtrack.jetbrains.com/issue/KT-48214
import fr.javatic.noteapp.Role
import fr.javatic.noteapp.database.schema.tables.references.USER
import fr.javatic.util.UUIDv4
import fr.javatic.util.create
import fr.javatic.yafull.utils.password.PasswordHasher
import fr.javatic.yafull.utils.password.PasswordStrengthEstimator
import fr.javatic.yafull.vertx.database.DatabaseConfig
import fr.javatic.yafull.vertx.database.jooq.Database
import fr.javatic.yafull.vertx.database.jooq.DatabaseOperation
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.awaitBlocking
import org.flywaydb.core.Flyway
import org.jooq.impl.DSL.value
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testcontainers.containers.PostgreSQLContainer


class DatabaseInitializer(val vertx: Vertx, config: DatabaseConfig) {
    private val config = resolveConfiguration(config)
    val database: Database = Database(vertx, this.config)

    suspend fun initialize(
        passwordStrengthEstimator: PasswordStrengthEstimator,
        passwordHasher: PasswordHasher,
        defaultAdminPassword: String?
    ) {
        createOrUpgradeSchema()
        createDefaultUserIfEmpty(passwordStrengthEstimator, passwordHasher, defaultAdminPassword)
    }

    private suspend fun createOrUpgradeSchema() {
        awaitBlocking {
            Flyway.configure()
                .dataSource(
                    config.computeJdbcUrl(),
                    config.user,
                    config.password
                ).load()
                .migrate()
        }
    }

    private suspend fun createDefaultUserIfEmpty(
        passwordStrengthEstimator: PasswordStrengthEstimator,
        passwordHasher: PasswordHasher,
        defaultAdminPassword: String?
    ) {
        val login = "admin"
        val password = defaultAdminPassword ?: awaitBlocking { passwordStrengthEstimator.generate() }

        val defaultUserCreated = database.withTransaction {
            val id = UUIDv4.create()
            val roles: Array<String?> = arrayOf(Role.ADMIN)
            val fullName = "Default User"
            val hashedPassword = passwordHasher.hash(password.toCharArray())

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
                .whereNotExists(select(value(1)).from(USER))
                .upsert() == DatabaseOperation.UpsertResult.INSERT
        }

        if (defaultUserCreated) {
            LOGGER.info("Created default user `$login` with password `${password}`")
        }
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(DatabaseInitializer::class.java)

        fun resolveConfiguration(databaseConfig: DatabaseConfig): DatabaseConfig.External = when (databaseConfig) {
            is DatabaseConfig.External -> databaseConfig
            is DatabaseConfig.TestContainer -> {
                val container = PostgreSQLContainer<Nothing>(databaseConfig.image)
                Runtime.getRuntime().addShutdownHook(object : Thread() {
                    override fun run() {
                        if (container.isRunning) {
                            LOGGER.info("Container is stopping (please wait)...")
                            container.stop()
                            LOGGER.info("Container is stopped")
                        }
                    }
                })
                container.start()
                DatabaseConfig.External.parseJdbcUrl(container.jdbcUrl)
                    .copy(user = container.username, password = container.password)
            }
        }
    }
}
