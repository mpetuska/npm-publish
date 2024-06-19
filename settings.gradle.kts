plugins {
  id("com.gradle.develocity") version "+"
}

develocity {
  buildScan{
    termsOfUseUrl = "https://gradle.com/terms-of-service"
    termsOfUseAgree = "yes"
  }
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
