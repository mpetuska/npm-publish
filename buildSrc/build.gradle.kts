plugins {
  `kotlin-dsl`
}

repositories {
  mavenLocal()
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$embeddedKotlinVersion")
  implementation("com.github.jakemarsden:git-hooks-gradle-plugin:_")
  implementation("com.gradle.publish:plugin-publish-plugin:_")
  implementation("org.jetbrains.dokka:dokka-gradle-plugin:_")
  implementation("io.github.gradle-nexus:publish-plugin:_")
  implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:_")
  implementation("dev.petuska:container-tasks-gradle-plugin:_")
}
