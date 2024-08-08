pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
  }

  includeBuild("./build-conventions")
}

plugins {
  id("settings")
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
  }
}

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

rootProject.name = "npm-publish"
//include(
//  "npm-publish-gradle-plugin",
//  "npm-publish-docs",
//)
