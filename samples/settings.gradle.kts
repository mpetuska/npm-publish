plugins {
  id("com.gradle.enterprise") version "+"
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
  }
}

rootProject.name = "samples"

includeBuild("../")
includeBuild("../build-conventions")
include(
  ":publish-to-github-packages",
  ":no-kotlin-plugin",
  ":local-ts-consumer:kt",
)
