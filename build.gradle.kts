plugins {
    val kotlin_version = "1.5.21"
    kotlin("jvm") version kotlin_version apply false
    //kotlin("js") version kotlin_version apply false
    kotlin("multiplatform") version kotlin_version apply false
    kotlin("plugin.serialization") version kotlin_version apply false
    //id("org.jetbrains.compose") version "1.0.0-alpha4-build315" apply false
    id("org.jetbrains.compose") version "0.0.0-SNAPSHOT" apply false
}

try {
    val properties = java.util.Properties()
    properties.load(java.io.FileReader(rootProject.file("gradle.local.properties"), Charsets.UTF_8))
    ext["local"] = properties
} catch (e: java.io.IOException) {
    throw GradleException("File `gradle.local.properties` is missing or unreadable", e)
}


allprojects {
    group = "fr.javatic.yafull"
    version = "0.1-SNAPSHOT"
}
