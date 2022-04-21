plugins {
  id("de.fayard.refreshVersions") version "0.40.1"
  id("com.gradle.enterprise") version "3.8.1"
}

rootProject.name = "npm-publish"
include("npm-publish-gradle-plugin")
rootDir.resolve("../jekyll-gradle").takeIf(File::exists)?.let(File::getAbsolutePath)?.let(::includeBuild)
