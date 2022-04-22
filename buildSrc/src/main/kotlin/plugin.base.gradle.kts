plugins {
  id("io.gitlab.arturbosch.detekt")
  idea
}

dependencies {
  detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:_")
}

detekt {
  config.from(rootDir.resolve("detekt.yml"))
  buildUponDefaultConfig = true
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
