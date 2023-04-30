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

rootProject.name = "sandbox"
includeBuild("../")
includeBuild("../build-conventions")
include(
  ":mpp",
  ":both",
  ":browser",
  ":empty",
  ":node",
)
