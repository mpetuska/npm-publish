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
