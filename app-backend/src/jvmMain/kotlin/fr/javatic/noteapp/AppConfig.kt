package fr.javatic.noteapp

import fr.javatic.yafull.rest.plugin.jwt.JwtConfig
import fr.javatic.yafull.utils.password.PasswordCheckerConfig
import fr.javatic.yafull.utils.password.PasswordHasherConfig
import fr.javatic.yafull.utils.password.PasswordStrengthEstimatorConfig
import fr.javatic.yafull.vertx.cors.CorsConfig
import fr.javatic.yafull.vertx.database.DatabaseConfig
import kotlinx.serialization.Serializable

@Serializable
data class AppConfig(
    val dev: Boolean,
    val keystore: KeyStoreConfig,
    val database: DatabaseConfig,
    val password: PasswordConfig,
    val web: WebConfig,
) {
    @Serializable
    data class KeyStoreConfig(
        val path: String,
        val password: String? = null,
    )

    @Serializable
    data class WebConfig(
        val port: Int = 2550,
        val https: Https?,
        val jwt: JwtConfig,
        val cors: CorsConfig?,
    ) {
        @Serializable
        data class Https(
            val port: Int = 2553,
            val certAlias: String,
            val certAliasPassword: String? = null,
        )

    }

    @Serializable
    data class PasswordConfig(
        val checker: PasswordCheckerConfig,
        val strengthEstimator: PasswordStrengthEstimatorConfig,
        val hasher: PasswordHasherConfig,
        val defaultAdminPassword: String? = null
    )


}
