@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        /*google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }*/
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


