plugins {
  id("de.fayard.refreshVersions") version "0.40.1"
  id("com.gradle.enterprise") version "3.8.1"
}

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

refreshVersions {
  extraArtifactVersionKeyRules(rootDir.resolve("versions.rules"))
}

rootProject.name = "npm-publish"
include(
  "npm-publish-gradle-plugin",
  "npm-publish-docs",
)
