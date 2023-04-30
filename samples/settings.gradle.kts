plugins {
  id("com.gradle.enterprise") version "3.13"
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
)
