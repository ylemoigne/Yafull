// Exist because of
// https://youtrack.jetbrains.com/issue/KTIJ-12934
plugins {
    java
    application
}

val localProperties: java.util.Properties = rootProject.ext["local"] as java.util.Properties

dependencies {
    runtimeOnly(projects.appBackend)
}

application {
    mainClass.set(localProperties.getProperty("mainClassName"))
    applicationDefaultJvmArgs = listOf("-Dfile.encoding=UTF8")
    applicationName = "yafull-note-app"
}
distributions {
    main {
        contents {
            from(file("rootdir"))
        }
    }
}
