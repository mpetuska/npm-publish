plugins {
  id("com.gradle.enterprise") version "3.14.1"
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
  }
}

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

rootProject.name = "npm-publish"
includeBuild("build-conventions")
include(
  "npm-publish-gradle-plugin",
  "npm-publish-docs",
)
