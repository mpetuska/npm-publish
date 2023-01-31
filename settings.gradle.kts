plugins {
  id("de.fayard.refreshVersions") version "0.51.0"
  id("com.gradle.enterprise") version "3.12.3"
}

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

refreshVersions {
  versionsPropertiesFile = rootDir.resolve("gradle/versions.properties")
  extraArtifactVersionKeyRules(rootDir.resolve("gradle/versions.rules"))
}

rootProject.name = "npm-publish"
includeBuild("build-conventions")
include(
  "npm-publish-gradle-plugin",
  "npm-publish-docs",
)
