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
  config.from(projectDir.resolve("detekt.yml"))
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
