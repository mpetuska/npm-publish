import io.gitlab.arturbosch.detekt.Detekt

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

tasks.withType<Detekt>().configureEach {
  reports {
    html.required.set(true) // observe findings in your browser with structure and code snippets
    xml.required.set(true) // checkstyle like format mainly for integrations like Jenkins
    txt.required.set(true) // similar to the console output, contains issue signature to manually edit baseline files
    sarif.required.set(true) // standardized SARIF format (https://sarifweb.azurewebsites.net/) to support integrations with Github Code Scanning
  }
}
