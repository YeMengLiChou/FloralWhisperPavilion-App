
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

rootProject.name = "FloralWhisperPavilion"
include(":app")
include(":core:common")
include(":core:network")
include(":core:datastore")
include(":core:data")
include(":core:database")
include(":core:model")
include(":core:ui")

include(":feature:login")
include(":feature:user-order")
include(":feature:menu")
include(":feature:home")
include(":feature:mine")
include(":feature:shop")
include(":feature:employee-order")