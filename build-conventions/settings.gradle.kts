plugins {
  id("com.gradle.enterprise") version "+"
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      from(files("../gradle/libs.versions.toml"))
    }
  }
  repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
  }
}

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
