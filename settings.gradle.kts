plugins {
  id("de.fayard.refreshVersions") version "0.10.0"
  id("com.gradle.enterprise") version "3.6.1"
}

rootProject.name = "npm-publish"
includeBuild("sandbox") {
  dependencySubstitution {
    substitute(module("dev.petuska:npm-publish")).with(project(":"))
  }
}
