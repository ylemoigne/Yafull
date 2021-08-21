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
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.yafullWithCompose)
                implementation(projects.yafullWithSerialization)

                implementation(libs.kotlinx.datetime)
            }
        }

        val jvmMain by getting {
            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
            languageSettings.useExperimentalAnnotation("kotlin.time.ExperimentalTime")
            languageSettings.useExperimentalAnnotation("kotlinx.serialization.ExperimentalSerializationApi")

            dependencies {
                implementation(projects.appJooqSchema)

                implementation(kotlin("stdlib-jdk8"))
                implementation(kotlin("reflect"))
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)

                // https://youtrack.jetbrains.com/issue/KMA-230
                implementation(project.dependencies.platform(libs.vertx.platform))
                implementation(libs.vertx.web)
                implementation(libs.vertx.auth.jwt)
                implementation(libs.vertx.kotlin.lang)
                implementation(libs.vertx.kotlin.coroutines)
                implementation(libs.vertx.reactivedriver.pg)
                implementation(libs.vertx.reactivedriver.mysql) // It works also for mariadb


                implementation(libs.slf4j.api)
                implementation(libs.slf4j.from.jul)
                implementation(libs.logback)

                implementation(libs.flyway)

                implementation(libs.picocli)

                implementation(libs.testcontainers.postgres)
//                implementation(libs.testcontainers.mariadb)
//                implementation(libs.testcontainers.mysql)

                runtimeOnly(libs.jdbc.postgres)
//                runtimeOnly(libs.jdbc.mariadb)
//                runtimeOnly(libs.jdbc.mysql)

                runtimeOnly("org.webjars.npm:swagger-ui-dist:3.51.2")
            }
        }
        val jvmTest by getting {
//            dependencies {
//                testImplementation("io.vertx:vertx-junit5")
//                testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
//            }
        }
    }
}

tasks.named<Jar>("jvmJar") {
    into("META-INF/frontend") {
        from(project(":app-frontend").tasks.named("jsBrowserDistribution"))
    }
    manifest {
        attributes("Main-Class" to localProperties.getProperty("mainClassName"))
    }
}

// https://youtrack.jetbrains.com/issue/KTIJ-12934
tasks.create<JavaExec>("run") {
    workingDir(project(":app-assemble").projectDir.resolve("rootdir"))
    systemProperty("file.encoding", "UTF8")

    mainClass.set(localProperties.getProperty("mainClassName"))
    args("start")

    classpath(tasks.named("compileKotlinJvm"))
    classpath(tasks.named("jvmProcessResources"))
    classpath(configurations.named("jvmRuntimeClasspath"))
}

