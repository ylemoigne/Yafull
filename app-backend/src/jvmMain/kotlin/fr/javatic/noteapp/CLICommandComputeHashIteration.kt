package fr.javatic.noteapp

import fr.javatic.yafull.utils.password.PasswordHasher
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import picocli.CommandLine

@CommandLine.Command(
    name = "compute-hash-iteration", description = ["Compute password hashing Argon2 iteration parameter."], usageHelpAutoWidth = true, sortOptions = false
)
class CLICommandComputeHashIteration : Runnable {
    @CommandLine.Parameters(
        description = ["Maximum number of milliseconds the hash function must take."],
        paramLabel = "max-millis",
        arity = "1",
        showDefaultValue = CommandLine.Help.Visibility.ALWAYS
    )
    var maxMillis: Long = 1000L

    @CommandLine.Option(
        names = ["m", "memory"],
        description = ["Sets memory usage to x kibibyte."],
        paramLabel = "memory",
        arity = "0..1",
        showDefaultValue = CommandLine.Help.Visibility.ALWAYS
    )
    var memory: Int = PasswordHasher.DEFAULT_MEMORY

    @CommandLine.Option(
        names = ["p", "parallelism"],
        description = ["Number of threads and compute lanes."],
        paramLabel = "parallelism",
        arity = "0..1",
        showDefaultValue = CommandLine.Help.Visibility.ALWAYS
    )
    var parallelism: Int = PasswordHasher.DEFAULT_PARALLELISM

    override fun run() {
        println("Iterations: ${PasswordHasher.computeIteration(maxMillis, memory, parallelism)}")
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(CLICommandComputeHashIteration::class.java)
    }
}
