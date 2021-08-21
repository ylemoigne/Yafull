package fr.javatic.yafull.vertx.database

import kotlinx.serialization.Serializable
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.DurationUnit

@Serializable
sealed class DatabaseConfig {
    @Serializable
    data class TestContainer(val image: String) : DatabaseConfig()

    @Serializable
    data class External(
        val type: Type,
        val host: String,
        val database: String,
        val user: String? = null,
        val password: String? = null,
        val port: Int = type.defaultPort,
        val ssl: Boolean? = null,
        val parameters: Map<String, String?> = emptyMap(),
        val timeoutInSeconds: Int? = 3,
        val maxPoolSize: Int? = null,
        val maxWaitQueueSize: Int? = null
    ) : DatabaseConfig() {
        fun computeJdbcUrl(
            includeUser: Boolean = false,
            includePassword: Boolean = false,
            includeTypeDefaultParameters: Boolean = true,
        ): String {
            val timeoutParameters = buildMap<String, String> {
                if (ssl != null) {
                    put(type.sslProperty, ssl.toString())
                }
                if (timeoutInSeconds != null) {
                    val timeoutValue =
                        maxOf(Duration.seconds(timeoutInSeconds).toDouble(type.timeoutUnit), 1.0).roundToInt()
                            .toString()
                    for (p in type.timoutProperties) {
                        put(p, timeoutValue)
                    }
                }
            }

            val typeDefaultParameters = if (!includeTypeDefaultParameters) {
                emptyMap()
            } else {
                type.defaultParameters
            }

            val allParameters = timeoutParameters + typeDefaultParameters + this.parameters
            val parametersAsString = allParameters.map { (k, v) -> "$k=$v" }.joinToString("&")
            val queryPart = if (parametersAsString.isEmpty()) "" else "?$parametersAsString"

            val credentialsPart = if (!includeUser && !includePassword) {
                ""
            } else {
                val userPart = if (includeUser) this.user else ""
                val passwordPart = if (includePassword) this.password else ""
                "$userPart:$passwordPart@"
            }

            return "jdbc:${type.jdbcProtocol}://${credentialsPart}${host}:${port}/$database$queryPart"
        }


        companion object {
            fun parseJdbcUrl(url: String): External {
                val urlWithoutJdbcPrefix = url.removePrefix("jdbc:")
                val protocolPart = StringBuilder()
                val credentialsOrHostnamePart = StringBuilder()
                val credentialsPart = StringBuilder()
                val hostnamePart = StringBuilder()
                val databaseNamePart = StringBuilder()
                val queryPart = StringBuilder()

                var skipNext = 0
                var currentPart = protocolPart
                for (c in urlWithoutJdbcPrefix) {
                    if (skipNext > 0) {
                        skipNext--
                        continue
                    }
                    if (currentPart === protocolPart && c == ':') {
                        currentPart = credentialsOrHostnamePart
                        skipNext = 2
                        continue
                    }
                    if (currentPart === credentialsOrHostnamePart && c == '@') {
                        credentialsPart.append(credentialsOrHostnamePart)
                        currentPart = hostnamePart
                        continue
                    }
                    if ((currentPart === credentialsOrHostnamePart || currentPart === hostnamePart) &&
                        c == '/'
                    ) {
                        if (currentPart === credentialsOrHostnamePart) hostnamePart.append(credentialsOrHostnamePart)
                        currentPart = databaseNamePart
                        continue
                    }
                    if (currentPart === databaseNamePart && c == '?') {
                        currentPart = queryPart
                        continue
                    }

                    currentPart.append(c)
                }

                val type = Type.values().find { it.jdbcProtocol.equals(protocolPart.toString(), true) }
                    ?: throw IllegalArgumentException("The jdbc protocol '${protocolPart}' is not supported")

                val user = if (credentialsPart.isEmpty()) null else credentialsPart.toString().substringBefore(':')
                val password =
                    if (credentialsPart.isEmpty()) null else credentialsPart.toString().substringAfter(':', "")
                        .ifEmpty { null }

                val hostnameAndMaybePort = hostnamePart.split(':')
                val (hostname, port) = when (hostnameAndMaybePort.size) {
                    1 -> hostnameAndMaybePort[0] to type.defaultPort
                    2 -> hostnameAndMaybePort[0] to hostnameAndMaybePort[1].toInt()
                    else -> throw IllegalArgumentException("Failed to parse hostname and port from '${hostnamePart}'")
                }

                val parameters = buildMap<String, String?> {
                    for (paramPair in queryPart.split("&")) {
                        val params = paramPair.split('=')
                        when (params.size) {
                            1 -> put(params[0], null)
                            2 -> put(params[0], params[1])
                            else -> throw IllegalArgumentException("Failed to parse parameter key and value from '$paramPair'")
                        }
                    }
                }

                val timeoutEntry = parameters.entries.find { type.timoutProperties.contains(it.key) }
                val timeout = timeoutEntry?.value?.let {
                    // With kotlin 1.5.21, is doesn't seem possible to create duration from value and DurationUnit
                    // simplify when it will be possible
                    java.time.Duration.of(it.toLong(), type.timeoutUnit.toChronoUnit()).toSeconds().toInt()
                }

                return External(
                    type,
                    hostname,
                    databaseNamePart.toString(),
                    user ?: parameters[type.loginProperty],
                    password ?: parameters[type.passwordProperty],
                    port,
                    parameters[type.sslProperty]?.toBoolean() ?: type.sslDefault,
                    parameters,
                    timeout
                )
            }
        }
    }

    @Serializable
    enum class Type(
        val jdbcProtocol: String,
        val defaultPort: Int,
        val urlDatasourceProperty: String,
        val loginProperty: String,
        val passwordProperty: String,
        val sslProperty: String,
        val sslDefault: Boolean,
        val timoutProperties: Set<String>,
        val timeoutUnit: DurationUnit,
        val defaultParameters: Map<String, String>
    ) {
        MYSQL(
            "mysql",
            3306,
            "URL",
            "user",
            "password",
            "useSsl",
            false,
            setOf("connectTimeout"),
            DurationUnit.MILLISECONDS,
            mapOf("disableMariaDbDriver" to "true")
        ),
        MARIADB(
            "mariadb",
            3306,
            "Url",
            "user",
            "password",
            "useSsl",
            false,
            setOf("connectTimeout"),
            DurationUnit.MILLISECONDS,
            emptyMap()
        ),
        POSTGRESQL(
            "postgresql",
            5432,
            "URL",
            "user",
            "password",
            "ssl",
            false,
            setOf("connectTimeout", "loginTimeout"),
            DurationUnit.SECONDS,
            emptyMap()
        );
    }
}
