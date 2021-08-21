package fr.javatic.yafull.utils.password

import kotlinx.serialization.Serializable

@Serializable
data class PasswordHasherConfig(
    val iterations: Int,
    val memory: Int = 65536,
    val parallelism: Int = 1,
)
