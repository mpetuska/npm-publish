pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
  }

  includeBuild("../build-conventions")
}

plugins {
  id("settings")
}

rootProject.name = "sandbox"
includeBuild("../")
include(
  ":mpp",
  ":both",
  ":browser",
  ":empty",
  ":node",
)
