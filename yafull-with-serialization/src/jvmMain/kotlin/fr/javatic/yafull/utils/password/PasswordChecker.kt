package fr.javatic.yafull.utils.password

import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.concurrent.ConcurrentHashMap

class PasswordChecker(
    private val maxAttemptBeforeLock: Int? = 5,
    private val lockoutDurationInSeconds: Int = 30 * 60,
    private val passwordHasher: PasswordHasher
) {
    private val DEFAULT_ATTEMPT_STATE = AttemptState(
        maxAttemptBeforeLock ?: Int.MAX_VALUE,
        null
    )

    fun check(identifier: String, credential: String, hashedPassword: String?): AuthResult {
        val userAttemptState = attemptStates[identifier] ?: DEFAULT_ATTEMPT_STATE
        val lockedUntil = userAttemptState.lockInstant?.plus(
            lockoutDurationInSeconds.toLong(),
            ChronoUnit.SECONDS
        )
        if (lockedUntil != null) {
            return AuthResult.Unauthorized("Account locked until ${DateTimeFormatter.ISO_INSTANT.format(lockedUntil)}")
        }

        if (hashedPassword != null && passwordHasher.match(credential.toCharArray(), hashedPassword)) {
            return AuthResult.Ok
        }

        if (maxAttemptBeforeLock == null) {
            return AuthResult.Unauthorized("Invalid Credentials")
        }

        val newTriesLeft = userAttemptState.triesLeft - 1
        val newLockInstant = if (newTriesLeft > 0) null else Instant.now()
        attemptStates[identifier] = AttemptState(newTriesLeft, newLockInstant)

        return AuthResult.Unauthorized("Invalid Credentials, $newTriesLeft tries left")
    }

    sealed class AuthResult {
        object Ok : AuthResult()
        data class Unauthorized(val message: String) : AuthResult()
    }

    data class AttemptState(val triesLeft: Int, val lockInstant: Instant?)

    companion object {
        private val attemptStates = ConcurrentHashMap<String, AttemptState>()
    }
}
