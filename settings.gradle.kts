@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "AtmoSphere"

include(":app")

// Domain Modules
include(":domain:region")
include(":domain:weather")

// Framework Modules
include(":framework:core")
include(":framework:region")
include(":framework:weather")

// Feature Module
include(":feature:common")
include(":feature:home")
include(":feature:detail")

// Test Module
include(":test:unit")



