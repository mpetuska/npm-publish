plugins {
  `kotlin-dsl`
}

repositories {
  mavenLocal()
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  implementation("com.github.jakemarsden:git-hooks-gradle-plugin:_")
  implementation("com.diffplug.spotless:spotless-plugin-gradle:_")
  implementation("com.gradle.publish:plugin-publish-plugin:_")
  implementation("org.jetbrains.dokka:dokka-gradle-plugin:_")
  implementation("io.github.gradle-nexus:publish-plugin:_")
}
