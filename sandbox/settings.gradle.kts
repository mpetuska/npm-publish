rootProject.name = "sandbox"
pluginManagement {
  repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
  }
}

include(":node", ":browser", ":mpp")
includeBuild("../") {
  dependencySubstitution {
    substitute(module("dev.petuska:npm-publish")).with(project(":"))
    substitute(module("dev.petuska.npm.publish:dev.petuska.npm.publish.gradle.plugin")).with(project(":"))
  }
}

