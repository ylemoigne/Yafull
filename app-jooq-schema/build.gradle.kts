import org.jooq.meta.jaxb.ForcedType

plugins {
    kotlin("jvm")
    id("nu.studer.jooq") version "6.0"
    id("org.flywaydb.flyway") version "7.12.0"
}

val localProperties: java.util.Properties = rootProject.ext["local"] as java.util.Properties
val jooqOutputDir = "src/main/kotlin-generated"

dependencies {
    jooqGenerator(libs.jdbc.postgres)
//    jooqGenerator(libs.jdbc.mariadb)
//    jooqGenerator(libs.jdbc.mysql)

    api(libs.jooq)
    api(libs.kotlinx.datetime)
    api(projects.yafullWithSerialization)
}

kotlin {
    sourceSets {
        val main by getting {
            kotlin.srcDir(jooqOutputDir)
        }
    }
}

flyway {
    driver = localProperties.getProperty("jdbcDriver")
    url = localProperties.getProperty("jdbcUrl")
    user = localProperties.getProperty("jdbcUser")
    password = localProperties.getProperty("jdbcPassword")
    schemas = arrayOf(localProperties.getProperty("jdbcSchema"))

    configurations = arrayOf("jooqGenerator")
}

jooq {
    version.set(libs.versions.jooq)

    configurations {
        create("main") {
            generateSchemaSourceOnCompilation.set(false)
            jooqConfiguration.apply {
                jdbc.apply {
                    driver = localProperties.getProperty("jdbcDriver")
                    url = localProperties.getProperty("jdbcUrl")
                    user = localProperties.getProperty("jdbcUser")
                    password = localProperties.getProperty("jdbcPassword")
                }
                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"
                    database.apply {
                        // See https://www.jooq.org/doc/latest/manual/code-generation/codegen-advanced/codegen-config-database/codegen-database-name/
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        //name = "org.jooq.meta.mariadb.MariaDBDatabase"
                        //name = "org.jooq.meta.mysql.MySQLDatabase"
                        inputSchema = localProperties.getProperty("jdbcSchema")

                        excludes = "flyway_schema_history"

                        forcedTypes.addAll(
                            listOf(
                                ForcedType()
                                    .withUserType("fr.javatic.util.UUIDv4")
                                    .withConverter("fr.javatic.jooq.UUIDv4Converter")
                                    .withIncludeTypes("uuid"),
                                ForcedType()
                                    .withUserType("kotlinx.datetime.LocalDateTime")
                                    .withConverter("fr.javatic.jooq.KotlinLocalDateTimeConverter")
                                    .withIncludeTypes("timestamp"),
                                ForcedType()
                                    .withUserType("kotlinx.datetime.LocalDate")
                                    .withConverter("fr.javatic.jooq.KotlinLocalDateConverter")
                                    .withIncludeTypes("date"),
                                ForcedType()
                                    .withUserType("kotlinx.datetime.Instant")
                                    .withConverter("fr.javatic.jooq.KotlinInstantConverter")
                                    .withIncludeTypes("instant")
                            )
                        )
                    }
                    generate.apply {
                        isGlobalCatalogReferences = false
                        isGlobalSchemaReferences = false
                        isRecords = false
                        isJavaTimeTypes = true
                    }
                    target.apply {
                        encoding = "UTF-8"
                        packageName = "fr.javatic.noteapp.database.schema"
                        directory = jooqOutputDir
                    }
                }
            }
        }
    }
}

tasks.named("generateJooq") {
    dependsOn(tasks.named("flywayClean"))
    dependsOn(tasks.named("flywayMigrate"))
}
