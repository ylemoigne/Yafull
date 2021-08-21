package fr.javatic.yafull.vertx.database.jooq

import fr.javatic.yafull.vertx.database.DatabaseConfig
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.mysqlclient.mySQLConnectOptionsOf
import io.vertx.kotlin.pgclient.pgConnectOptionsOf
import io.vertx.kotlin.sqlclient.poolOptionsOf
import io.vertx.mysqlclient.MySQLPool
import io.vertx.pgclient.PgPool
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.conf.ParamType
import org.jooq.conf.Settings
import org.jooq.impl.DSL
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.time.Duration
import kotlin.time.DurationUnit
import io.vertx.mysqlclient.SslMode as MySqlSSL
import io.vertx.pgclient.SslMode as PgSSL

class Database(
    vertx: Vertx,
    config: DatabaseConfig.External
) : AutoCloseable {
    init {
        if (config.parameters.isNotEmpty()) {
            LOGGER.warn("Parameters will be ignored")
        }
    }

    val dsl: DSLContext = when (config.type) {
        DatabaseConfig.Type.MYSQL -> DSL.using(
            SQLDialect.MYSQL,
            Settings()
                .withRenderSchema(false)
        )
        DatabaseConfig.Type.MARIADB -> DSL.using(
            SQLDialect.MARIADB,
            Settings()
                .withRenderSchema(false)
        )
        DatabaseConfig.Type.POSTGRESQL -> DSL.using(
            SQLDialect.POSTGRES,
            Settings()
                .withRenderSchema(false)
                .withParamType(ParamType.INLINED)
        )
    }
    private val pool = when (config.type) {
        DatabaseConfig.Type.MYSQL, DatabaseConfig.Type.MARIADB -> MySQLPool.pool(
            vertx,
            mySQLConnectOptionsOf(
                port = config.port,
                host = config.host,
                user = config.user,
                password = config.password,
                database = config.database,
                ssl = config.ssl,
                sslMode = when (config.ssl) {
                    true -> MySqlSSL.REQUIRED
                    null -> MySqlSSL.PREFERRED
                    false -> MySqlSSL.DISABLED
                },
                connectTimeout = config.timeoutInSeconds?.let { Duration.seconds(it).toInt(DurationUnit.MILLISECONDS) }
            ),
            poolOptionsOf(
                maxSize = config.maxPoolSize, maxWaitQueueSize = config.maxWaitQueueSize
            )
        )
        DatabaseConfig.Type.POSTGRESQL -> PgPool.pool(
            vertx,
            pgConnectOptionsOf(
                port = config.port,
                host = config.host,
                user = config.user,
                password = config.password,
                database = config.database,
                ssl = config.ssl,
                sslMode = when (config.ssl) {
                    true -> PgSSL.REQUIRE
                    null -> PgSSL.PREFER
                    false -> PgSSL.DISABLE
                },
                connectTimeout = config.timeoutInSeconds?.let { Duration.seconds(it).toInt(DurationUnit.MILLISECONDS) }
            ),
            poolOptionsOf(
                maxSize = config.maxPoolSize, maxWaitQueueSize = config.maxWaitQueueSize
            )
        )
    }

    suspend fun <T> withTransaction(autocommit: Boolean = true, consumer: suspend DatabaseOperation.() -> T): T {
        val con = pool.connection.await()
        return try {
            val tx = con.begin().await()
            val operation = DatabaseOperation(con, dsl)
            val result = operation.consumer()
            if (autocommit) {
                tx.commit().await()
            }
            result
        } finally {
            con.close().await()
        }
    }

    suspend fun <T> withConnection(consumer: suspend DatabaseOperation.() -> T): T {
        val con = pool.connection.await()
        return try {
            val operation = DatabaseOperation(con, dsl)
            operation.consumer()
        } finally {
            con.close()
        }
    }

    override fun close() {
        pool.close()
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(Database::class.java)
    }
}
