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

rootProject.name = "samples"

includeBuild("../")
includeBuild("../build-conventions")
include(
  ":publish-to-github-packages",
  ":no-kotlin-plugin",
  ":local-ts-consumer:kt",
)
