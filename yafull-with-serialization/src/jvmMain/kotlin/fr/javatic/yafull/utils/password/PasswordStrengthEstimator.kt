package fr.javatic.yafull.utils.password

import com.nulabinc.zxcvbn.Zxcvbn
import java.security.SecureRandom
import java.util.*
import kotlin.streams.asSequence


class PasswordStrengthEstimator(
    passwordHasher: PasswordHasher,
    private val exclusionKeywords: List<String> = emptyList(),
    private val minimumScore: Int = 2,
    minimumLength: Int = 8,
) {
    constructor(passwordHasher: PasswordHasher, config: PasswordStrengthEstimatorConfig) : this(
        passwordHasher,
        config.exclusionKeywords,
        config.minimumScore,
        config.minimumLength,
    )

    private val zxcvbn = Zxcvbn()
    private val guessPerSecond = passwordHasher.guessPerSecond(minimumLength)

    val resourceBundle = ResourceBundle.getBundle("com/nulabinc/zxcvbn/messages", Locale.ROOT)

    fun generate(
        length: Int = 16,
        allowAmbiguousCharacter: Boolean = false,
        allowSpecialCharacter: Boolean = true
    ): String {
        var password: String
        do {
            password = SecureRandom().ints(0, 255).asSequence()
                .filter {
                    lowercaseLetter.contains(it) ||
                            uppercaseLetter.contains(it) ||
                            digit.contains(it) ||
                            (allowSpecialCharacter && specialChars.contains(it)) ||
                            (allowAmbiguousCharacter && ambiguousChars.contains(it))
                }
                .map { Char(it) }
                .take(length)
                .fold(StringBuilder(), StringBuilder::append)
                .toString()
        } while (estimate(password) is PasswordStrengthEstimation.Invalid)
        return password
    }

    fun estimate(password: String): PasswordStrengthEstimation {
        val strength = zxcvbn.measure(password, exclusionKeywords)
        val feedback = strength.feedback
        val localizedFeedback = feedback.withResourceBundle(resourceBundle)

        val crackTimeSeconds = strength.guesses / guessPerSecond

        val scoreExplain = when (strength.score) {
            0 -> "Weak"
            1 -> "Fair"
            2 -> "Good"
            3 -> "Strong"
            4 -> "Very Strong"
            else -> throw error("Score must be in [0..4] range")
        }

        return if (strength.score >= minimumScore) {
            PasswordStrengthEstimation.Valid(strength.score, scoreExplain, crackTimeSeconds)
        } else {
            PasswordStrengthEstimation.Invalid(
                strength.score,
                scoreExplain,
                crackTimeSeconds,
                minimumScore,
                localizedFeedback.warning,
                localizedFeedback.suggestions
            )
        }
    }

    companion object {
        val lowercaseLetter = ('a'..'z').map { it.code }.toIntArray()
        val uppercaseLetter = ('A'..'Z').map { it.code }.toIntArray()
        val digit = ('0'..'9').map { it.code }.toIntArray()
        val ambiguousChars = "l1IoO0".map { it.code }.toIntArray()
        val specialChars = "~!@#$%^&*()_+-={[}]|\\:;\"'<,>.?/'".map { it.code }.toIntArray()

    }
}
