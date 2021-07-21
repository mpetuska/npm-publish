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

include(":node", ":browser", ":both", ":mpp")
includeBuild("../") {
  dependencySubstitution {
    substitute(module("dev.petuska:npm-publish")).with(project(":"))
    substitute(module("dev.petuska.npm.publish:dev.petuska.npm.publish.gradle.plugin")).with(project(":"))
  }
}
