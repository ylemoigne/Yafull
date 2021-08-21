package fr.javatic.yafull.utils.password

import de.mkammerer.argon2.Argon2Factory
import de.mkammerer.argon2.Argon2Helper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.system.measureNanoTime

class PasswordHasher(
    private val iterations: Int,
    private val memory: Int = DEFAULT_MEMORY,
    private val parallelism: Int = DEFAULT_PARALLELISM,
) {
    constructor(config: PasswordHasherConfig) : this(config.iterations, config.memory, config.parallelism)

    fun match(password: CharArray, hashedPassword: String): Boolean = try {
        argon2.verify(hashedPassword, password)
    } finally {
        argon2.wipeArray(password)
    }

    fun hash(password: CharArray): String = try {
        argon2.hash(iterations, memory, parallelism, password)
    } finally {
        argon2.wipeArray(password)
    }

    fun guessPerSecond(length: Int): Double {
        val fakePassword = "a".repeat(length).toCharArray()
        val singleHashTimeInNanos = measureNanoTime { hash(fakePassword) }
        val parrallelHashTimeInNanos = singleHashTimeInNanos.toDouble() / parallelism.toDouble()
        return parrallelHashTimeInNanos * 1e-9
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(PasswordHasher::class.java)
        private val argon2: de.mkammerer.argon2.Argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id)

        const val DEFAULT_MEMORY = 65536
        const val DEFAULT_PARALLELISM = 1

        fun computeIteration(
            maxMillis: Long,
            memory: Int = DEFAULT_MEMORY,
            parallelism: Int = DEFAULT_PARALLELISM
        ): Int {
            LOGGER.info("Initializing Argon2 hasher : determining secure iteration parameter for current hardware with maximum hash time of ${maxMillis}ms (it may take time to compute)")
            return Argon2Helper.findIterations(argon2, maxMillis, memory, parallelism)
        }
    }
}
