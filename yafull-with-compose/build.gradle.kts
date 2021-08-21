plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
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

        val jsMain by getting {
            dependencies {
                implementation(projects.yafullWithSerialization)

                implementation(compose.web.core)
                implementation(compose.runtime)

                implementation(libs.kotlinx.coroutines.core)

                implementation(libs.microutils.kotlin.logging)

                implementation(npm("uikit", "3.7.1"))
            }
        }
    }
}
