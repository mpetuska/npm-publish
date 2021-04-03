import de.fayard.refreshVersions.bootstrapRefreshVersions

pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
  }
}

buildscript {
  repositories {
    gradlePluginPortal()
    mavenCentral()
  }
  dependencies { classpath("de.fayard.refreshVersions:refreshVersions:0.9.7") }
}
bootstrapRefreshVersions()

rootProject.name = "npm-publish"
