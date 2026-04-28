rootProject.name = "NuvoStore"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":composeApp")

// Core Modules
include(":core:designsystem")
include(":core:network")
include(":core:mvi")

// Data & Domain
include(":domain")
include(":data")

// Feature Modules
include(":feature:auth")
include(":feature:discovery")
include(":feature:catalog")
include(":feature:cart")
include(":feature:checkout")
