enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    versionCatalogs {
        create("libs") {
            version("jooq", "3.15.1")
            version("slf4j", "1.7.31")
            version("vertx", "4.1.2")
            version("kotest", "4.6.1")
            version("swagger-ui", "4.0.0-beta.4")
            //version("swagger-ui", "3.51.2")


            alias("kotlinx-datetime").to("org.jetbrains.kotlinx:kotlinx-datetime:0.2.1")
            alias("kotlinx-coroutines-core").to("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
            alias("kotlinx-serialization-json").to("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")

            alias("jdbc-postgres").to("org.postgresql:postgresql:42.2.23")
            alias("jdbc-mariadb").to("org.mariadb.jdbc:mariadb-java-client:2.7.3")
            alias("jdbc-mysql").to("mysql:mysql-connector-java:8.0.26")
            alias("jooq").to("org.jooq", "jooq").versionRef("jooq")

            alias("testcontainers-postgres").to("org.testcontainers:postgresql:1.15.3")
            alias("testcontainers-mariadb").to("org.testcontainers:mariadb:1.15.3")
            alias("testcontainers-mysql").to("org.testcontainers:mysql:1.15.3")

            alias("vertx-platform").to("io.vertx", "vertx-stack-depchain").versionRef("vertx")
            alias("vertx-web").to("io.vertx", "vertx-web").versionRef("vertx")
            alias("vertx-auth-jwt").to("io.vertx", "vertx-auth-jwt").versionRef("vertx")
            alias("vertx-kotlin-lang").to("io.vertx", "vertx-lang-kotlin").versionRef("vertx")
            alias("vertx-kotlin-coroutines").to("io.vertx", "vertx-lang-kotlin-coroutines").versionRef("vertx")
            alias("vertx-reactivedriver-pg").to("io.vertx", "vertx-pg-client").versionRef("vertx")
            alias("vertx-reactivedriver-mysql").to("io.vertx", "vertx-mysql-client").versionRef("vertx")

            alias("argon2").to("de.mkammerer:argon2-jvm:2.10.1")
            alias("zxcvbn").to("com.nulab-inc:zxcvbn:1.5.2")

            alias("slf4j-api").to("org.slf4j", "slf4j-api").versionRef("slf4j")
            alias("slf4j-from-jul").to("org.slf4j", "jul-to-slf4j").versionRef("slf4j")
            alias("logback").to("ch.qos.logback:logback-classic:1.2.5")

            alias("flyway").to("org.flywaydb:flyway-core:7.12.0")

            alias("picocli").to("info.picocli:picocli:4.6.1")

            alias("kotest-framework-engine").to("io.kotest", "kotest-framework-engine").versionRef("kotest")
            alias("kotest-assertions-core").to("io.kotest", "kotest-assertions-core").versionRef("kotest")
            alias("kotest-property").to("io.kotest", "kotest-property").versionRef("kotest")
            alias("kotest-framework-datatest").to("io.kotest", "kotest-framework-datatest").versionRef("kotest")
            alias("kotest-runner-junit5").to("io.kotest", "kotest-runner-junit5").versionRef("kotest")

            alias("webjars-swagger-ui").to("org.webjars.npm", "swagger-ui-dist").versionRef("swagger-ui")

            alias("microutils-kotlin-logging").to("io.github.microutils:kotlin-logging:2.0.10")
        }
    }
}

rootProject.name = "YafullStack-NoteApp"
include(
    "yafull-with-serialization",
    "yafull-with-compose",
    "app-assemble",
    "app-backend",
    "app-frontend",
    "app-jooq-schema",
)
