import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm")
  id("io.gitlab.arturbosch.detekt")
  idea
  if (System.getenv("CI") in arrayOf(null, "0", "false", "n")) {
    id("plugin.git-hooks")
  }
}

println("BUILD KOTLIN VERSION: $embeddedKotlinVersion")

detekt {
  config.from(rootDir.resolve("detekt.yml"))
  buildUponDefaultConfig = true
}

dependencies {
  detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:_")
}

idea {
  module {
    isDownloadJavadoc = true
    isDownloadSources = true
  }
}

repositories {
  mavenLocal()
  mavenCentral()
  gradlePluginPortal()
}

tasks {
  withType<KotlinCompile>().configureEach {
    kotlinOptions {
      jvmTarget = targetCompatibility
    }
  }
  withType<Test>().configureEach {
    useJUnitPlatform()
  }
}
