import de.fayard.refreshVersions.core.*

plugins {
  id("com.diffplug.spotless")
  idea
  if (System.getenv("CI") in arrayOf(null, "0", "false")) {
    id("plugin.git-hooks")
  }
}

idea {
  module {
    isDownloadJavadoc = true
    isDownloadSources = true
  }
}

spotless {
  val ktlintSettings = mapOf(
    "indent_size" to "2",
    "continuation_indent_size" to "4",
    "disabled_rules" to "no-wildcard-imports"
  )
  kotlin {
    target("src/**/*.kt")
    ktlint(versionFor("version.ktlint")).userData(ktlintSettings)
  }
  kotlinGradle {
    target("*.kts")
    ktlint(versionFor("version.ktlint")).userData(ktlintSettings)
  }
}

repositories {
  mavenLocal()
  mavenCentral()
  gradlePluginPortal()
}
