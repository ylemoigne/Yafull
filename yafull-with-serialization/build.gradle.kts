plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

val localProperties: java.util.Properties = rootProject.ext["local"] as java.util.Properties

kotlin {
    js(IR) {
        browser()
    }
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "16"
        }
    }
    sourceSets {
        all {
            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
            languageSettings.useExperimentalAnnotation("kotlin.time.ExperimentalTime")
            languageSettings.useExperimentalAnnotation("kotlinx.serialization.ExperimentalSerializationApi")
            languageSettings.useExperimentalAnnotation("io.kotest.common.ExperimentalKotest")
        }

        val commonMain by getting {
            dependencies {
                implementation(kotlin("reflect"))
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.coroutines.core)

                api(libs.kotlinx.serialization.json)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotest.framework.engine)
                implementation(libs.kotest.assertions.core)
                implementation(libs.kotest.property)
                implementation(libs.kotest.framework.datatest)
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(npm("uuid", "8.3.2"))
                implementation(npm("jwt-decode", "3.1.2"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation(kotlin("reflect"))
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)

                // https://youtrack.jetbrains.com/issue/KMA-230
                api(project.dependencies.platform(libs.vertx.platform))
                api(libs.vertx.web)
                api(libs.vertx.kotlin.lang)
                api(libs.vertx.kotlin.coroutines)
                api(libs.vertx.auth.jwt)
                api(libs.vertx.reactivedriver.pg)
                api(libs.vertx.reactivedriver.mysql) // It works also for mariadb

                api(libs.jooq)

                implementation(libs.slf4j.api)
                implementation(libs.slf4j.from.jul)
                implementation(libs.logback)

                implementation(libs.flyway)

                implementation(libs.picocli)


                implementation(libs.argon2)
                implementation(libs.zxcvbn)

                implementation(libs.testcontainers.postgres)
//                implementation(libs.testcontainers.mariadb)
//                implementation(libs.testcontainers.mysql)

                runtimeOnly(libs.jdbc.postgres)
//                runtimeOnly(libs.jdbc.mariadb)
//                runtimeOnly(libs.jdbc.mysql)
                runtimeOnly(libs.webjars.swagger.ui)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(libs.kotest.runner.junit5)
            }
        }

    }
}

tasks.withType<org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest>().configureEach {
    useJUnitPlatform()
}

val createSwaggerVersionFile by tasks.creating {
    description =
        "Generate a resource file containing swagger version, this file is mandatory to install swagger-ui handlers"
    val target = file("src/jvmMain/resources/swagger/version")

    inputs.property("swagger-ui.version", libs.versions.swagger.ui)
    outputs.file(target)

    doLast {
        target.writeText(libs.versions.swagger.ui.get())
    }
}

val jvmProcessResources by tasks.getting
jvmProcessResources.dependsOn(createSwaggerVersionFile)

