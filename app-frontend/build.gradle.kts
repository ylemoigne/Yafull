plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    js(IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
                configDirectory = file("webpack.conf.d")
                devServer?.proxy = mutableMapOf("/api" to "http://localhost:2550")
            }
        }
    }
    sourceSets {
        val jsMain by getting {
            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
            languageSettings.useExperimentalAnnotation("kotlin.time.ExperimentalTime")

            dependencies {
                implementation(projects.yafullWithCompose)
                implementation(projects.yafullWithSerialization)
                implementation(projects.appBackend)

                implementation(compose.web.core)
                implementation(compose.runtime)

                implementation(libs.kotlinx.coroutines.core)

                implementation(libs.microutils.kotlin.logging)

                compileOnly(devNpm("sass-loader", "12.1.0"))
                compileOnly(devNpm("sass", "1.32.13"))
            }
        }
    }
}

// https://youtrack.jetbrains.com/issue/KT-48273
// https://github.com/webpack/webpack-dev-server/releases
rootProject.plugins.withType(org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin::class.java) {
    rootProject.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().versions.webpackDevServer.version =
        "4.0.0-rc.1"
}

