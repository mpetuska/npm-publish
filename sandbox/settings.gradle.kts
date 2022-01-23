pluginManagement {
  repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    google()
  }
}
plugins {
  id("de.fayard.refreshVersions") version "0.30.2"
  id("com.gradle.enterprise") version "3.8.1"
}

rootProject.name = "sandbox"

includeBuild("../")
include(":node", ":browser", ":both", ":mpp")
