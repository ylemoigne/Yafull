package fr.javatic.yafull.utils.password

import kotlinx.serialization.Serializable

@Serializable
data class PasswordStrengthEstimatorConfig(
    val exclusionKeywords: List<String> = emptyList(),
    val minimumScore: Int = 2,
    val minimumLength: Int = 8,
)
