package fr.javatic.yafull.vertx.database.jooq

import fr.javatic.util.debug
import fr.javatic.util.trace
import io.vertx.kotlin.coroutines.await
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.RowSet
import io.vertx.sqlclient.SqlClient
import org.jooq.*
import org.jooq.impl.DSL
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class DatabaseOperation(private val client: SqlClient, private val dsl: DSLContext) : DSLContext by dsl {
    private val postgresUpsertDetection = DSL.field(DSL.field("xmax").eq("0")).`as`("upsert_is_insert")

    suspend fun InsertOnDuplicateSetMoreStep<*>.upsert(): UpsertResult {
        val upsertIsInsert = this.returning(postgresUpsertDetection)
            .perform()
            .single()
            .getBoolean("upsert_is_insert")
        return if (upsertIsInsert) UpsertResult.INSERT else UpsertResult.UPDATE
    }

    suspend fun InsertOnConflictConditionStep<*>.upsert(): UpsertResult {
        val upsertIsInsert = this.returning(postgresUpsertDetection)
            .perform()
            .single()
            .getBoolean("upsert_is_insert")
        return if (upsertIsInsert) UpsertResult.INSERT else UpsertResult.UPDATE
    }


    inline operator fun <reified T> Row.get(field: TableField<*, T>): T {
        @Suppress("UNCHECKED_CAST")
        val converter = field.converter as Converter<Any?, T>

        val value = this.get(converter.fromType(), field.name)
        return converter.from(value)
    }

    suspend fun Query.perform(): RowSet<Row> {
        val queryId = if (LOGGER.isTraceEnabled) "[${UUID.randomUUID()}] " else ""
        LOGGER.debug { "${queryId}Execute query: $sql" }
        val res = client.query(sql).execute().await()
        LOGGER.trace { "${queryId}Result set   : ${res.joinToString("\n\t") { it.toJson().encode() }}" }
        return res
    }

    enum class UpsertResult {
        INSERT,
        UPDATE
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(DatabaseOperation::class.java)
    }
}
