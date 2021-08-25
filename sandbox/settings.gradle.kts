pluginManagement {
  repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
  }
}
plugins {
  id("de.fayard.refreshVersions") version "0.10.1"
  id("com.gradle.enterprise") version "3.6.3"
}

rootProject.name = "sandbox"

includeBuild("../")
include(":node", ":browser", ":both", ":mpp")
