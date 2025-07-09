pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        id("com.android.application") version "8.1.2"
        id("org.jetbrains.kotlin.android") version "1.9.0"
        id("com.google.gms.google-services") version "4.4.1" // ✅ Esto permite que el plugin sea reconocido
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "taller prisila"
include(":app")
