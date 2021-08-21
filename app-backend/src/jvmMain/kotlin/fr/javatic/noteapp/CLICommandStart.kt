package fr.javatic.noteapp

import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import picocli.CommandLine
import java.nio.file.Path
import java.nio.file.Paths

@CommandLine.Command(
    name = "start", description = ["Start services."], usageHelpAutoWidth = true, sortOptions = false
)
class CLICommandStart : Runnable {
    @CommandLine.Option(
        names = ["--config", "-c"],
        description = ["Configuration file to use."],
        paramLabel = "configuration-file",
        required = false,
        showDefaultValue = CommandLine.Help.Visibility.ALWAYS
    )
    var configurationFile: Path = Paths.get("backend.json")

    override fun run(): Unit = runBlocking {
        val startInstant = System.currentTimeMillis()
        val initContext = InitializationContext.initialize(configurationFile)

        LOGGER.info("Deploy web server verticle")
        initContext.vertx.deployVerticle(
            WebServerVerticle(
                initContext.appConfig.dev,
                initContext.appConfig.web,
                initContext.passwordHasher,
                initContext.passwordStrengthEstimator,
                initContext.appConfig.password.checker,
                initContext.database,
                initContext.keystore
            )
        ).await()

        val startDuration = System.currentTimeMillis() - startInstant
        LOGGER.info("Initialization finished in ${startDuration}ms")
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(CLICommandStart::class.java)
    }
}
