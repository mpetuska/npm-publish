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

rootProject.name = "samples"

includeBuild("../")
includeBuild("../build-conventions")
include(
  ":publish-to-github-packages",
  ":no-kotlin-plugin",
  ":local-ts-consumer:kt",
)
