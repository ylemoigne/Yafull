package fr.javatic.noteapp

import io.netty.util.internal.logging.InternalLoggerFactory
import io.vertx.core.logging.SLF4JLogDelegateFactory
import picocli.CommandLine
import kotlin.system.exitProcess

object CLI {
    lateinit var args: Array<String>

    @JvmStatic
    fun main(args: Array<String>) {
        CLI.args = args

        System.setProperty("vertx.logger-delegate-factory-class-name", SLF4JLogDelegateFactory::class.java.name)
        InternalLoggerFactory.setDefaultFactory(io.netty.util.internal.logging.Slf4JLoggerFactory.INSTANCE)

        val exitCode = CommandLine(Global()).setColorScheme(CommandLine.Help.defaultColorScheme(CommandLine.Help.Ansi.AUTO))
            .execute(*args)

        if (exitCode != 0) {
            exitProcess(exitCode)
        }
    }

    @CommandLine.Command(
        name = "gmp-system-manager",
        versionProvider = VersionProvider::class,
        mixinStandardHelpOptions = true,
        synopsisSubcommandLabel = "COMMAND",
        subcommands = [CLICommandStart::class, CLICommandUser::class, CLICommandComputeHashIteration::class]
    )
    private class Global : Runnable {
        @CommandLine.Spec
        lateinit var spec: CommandLine.Model.CommandSpec

        override fun run() {
            throw CommandLine.ParameterException(spec.commandLine(), "Missing required COMMAND")
        }
    }


    object VersionProvider : CommandLine.IVersionProvider {
        override fun getVersion(): Array<String> = arrayOf("v0.0")
    }
}
