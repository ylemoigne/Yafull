package fr.javatic.yafull.utils.password

import kotlinx.serialization.Serializable

@Serializable
data class PasswordCheckerConfig(
    val maxAttemptBeforeLock: Int? = 5,
    val lockoutDurationInSeconds: Int = 30 * 60,
)
