package fr.javatic.noteapp

import fr.javatic.noteapp.database.schema.tables.references.USER
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.runBlocking
import picocli.CommandLine
import java.nio.file.Path
import java.nio.file.Paths

@CommandLine.Command(
    name = "user",
    description = ["Manage users"],
    mixinStandardHelpOptions = true,
    synopsisSubcommandLabel = "ACTION",
    subcommands = [
        CLICommandUser.Create::class,
        CLICommandUser.Update::class,
        CLICommandUser.Delete::class
    ]
)
class CLICommandUser : Runnable {
    @CommandLine.Spec
    lateinit var spec: CommandLine.Model.CommandSpec

    override fun run() {
        throw CommandLine.ParameterException(spec.commandLine(), "Missing required ACTION")
    }

    @CommandLine.Command(
        name = "create",
        description = ["Create user"],
        usageHelpAutoWidth = true,
        sortOptions = false
    )
    class Create : Runnable {
        @CommandLine.Option(
            names = ["--config", "-c"],
            description = ["Configuration file to use."],
            paramLabel = "configuration-file",
            required = false,
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS
        )
        var configurationFile: Path = Paths.get("backend.json")

        @CommandLine.Parameters(
            description = ["Login."],
            paramLabel = "login",
            index = "0",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS
        )
        lateinit var login: String

        @CommandLine.Parameters(
            description = ["Full name."],
            paramLabel = "full-name",
            index = "1",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS
        )
        lateinit var fullName: String

        @CommandLine.Parameters(
            description = ["Password."],
            paramLabel = "password",
            index = "2",
            interactive = true,
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS
        )
        lateinit var password: String

        @CommandLine.Parameters(
            description = ["Password."],
            paramLabel = "password",
            index = "3",
            arity = "*",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS
        )
        lateinit var roles: Array<String>

        override fun run() = runBlocking {
            val initContext = InitializationContext.initialize(configurationFile)

            @Suppress("UNCHECKED_CAST")
            val roles = roles as Array<String?>

            initContext.database.withTransaction {
                insertInto(USER)
                    .set(USER.LOGIN, login)
                    .set(USER.FULLNAME, fullName)
                    .set(USER.HASHED_PASSWORD, initContext.passwordHasher.hash(password.toCharArray()))
                    .set(USER.ROLES, roles)
                    .perform()
            }

            initContext.vertx.close().await()
            Unit
        }
    }

    @CommandLine.Command(
        name = "update",
        description = ["Update user"],
        usageHelpAutoWidth = true,
        sortOptions = false
    )
    class Update : Runnable {
        @CommandLine.Option(
            names = ["--config", "-c"],
            description = ["Configuration file to use."],
            paramLabel = "configuration-file",
            required = false,
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS
        )
        var configurationFile: Path = Paths.get("backend.json")

        @CommandLine.Parameters(
            description = ["Login."],
            paramLabel = "login",
            index = "0",
            interactive = true,
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS
        )
        lateinit var login: String

        @CommandLine.Option(
            names = ["--full-name", "-fn"],
            description = ["Full name."],
            paramLabel = "full-name",
            required = false,
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS
        )
        var fullName: String? = null

        @CommandLine.Option(
            names = ["--password", "-p"],
            description = ["Password."],
            paramLabel = "password",
            required = false,
            interactive = true,
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS
        )
        var password: String? = null

        override fun run() = runBlocking {
            val initContext = InitializationContext.initialize(configurationFile)

            initContext.database.withTransaction {
                fullName?.let {
                    update(USER)
                        .set(USER.FULLNAME, it)
                        .where(USER.LOGIN.eq(login))
                        .perform()
                }
                password?.let {
                    update(USER)
                        .set(USER.HASHED_PASSWORD, initContext.passwordHasher.hash(it.toCharArray()))
                        .where(USER.LOGIN.eq(login))
                        .perform()
                }
            }

            initContext.vertx.close().await()
            Unit
        }
    }

    @CommandLine.Command(
        name = "delete",
        description = ["Delete user"],
        usageHelpAutoWidth = true,
        sortOptions = false
    )
    class Delete : Runnable {
        @CommandLine.Option(
            names = ["--config", "-c"],
            description = ["Configuration file to use."],
            paramLabel = "configuration-file",
            required = false,
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS
        )
        var configurationFile: Path = Paths.get("backend.json")

        @CommandLine.Parameters(
            description = ["Login."],
            paramLabel = "login",
            index = "0",
            interactive = true,
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS
        )
        lateinit var login: String

        override fun run() = runBlocking {
            val initContext = InitializationContext.initialize(configurationFile)

            initContext.database.withTransaction {
                deleteFrom(USER).where(USER.LOGIN.eq(login)).perform()
            }

            initContext.vertx.close().await()
            Unit
        }
    }
}
