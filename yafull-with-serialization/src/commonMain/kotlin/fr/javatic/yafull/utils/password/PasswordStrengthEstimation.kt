package fr.javatic.yafull.utils.password

import kotlinx.serialization.Serializable

@Serializable
sealed class PasswordStrengthEstimation {
    abstract val score: Int
    abstract val scoreExplain: String
    abstract val crackTimeInSeconds: Double

    @Serializable
    class Valid(
        override val score: Int,
        override val scoreExplain: String,
        override val crackTimeInSeconds: Double
    ) : PasswordStrengthEstimation()

    @Serializable
    class Invalid(
        override val score: Int,
        override val scoreExplain: String,
        override val crackTimeInSeconds: Double,
        val minimumScore: Int,
        val warning: String,
        val suggestions: List<String>
    ) : PasswordStrengthEstimation()
}
