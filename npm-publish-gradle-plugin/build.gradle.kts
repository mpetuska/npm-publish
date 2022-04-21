import org.jetbrains.dokka.gradle.DokkaTask

plugins {
  id("plugin.common")
  id("plugin.publishing")
  id("dev.petuska.jekyll")
}

description = """
  A maven-publish alternative for NPM package publishing.
  Integrates with kotlin JS/MPP plugins (if applied) to automatically
  setup publishing to NPM repositories for all JS targets.
""".trimIndent()

kotlin {
  explicitApi()
  dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:_")
    implementation("com.google.code.gson:gson:_")
    testImplementation(Testing.kotest.assertions.core)
    testImplementation(Testing.kotest.assertions.json)
    testImplementation(Testing.junit.jupiter.api)
    testRuntimeOnly(Testing.junit.jupiter.engine)
  }
}

jekyll {
  environment.put("GIT_DISCOVERY_ACROSS_FILESYSTEM", "true")
}

tasks {
  withType<dev.petuska.jekyll.task.JekyllBuildTask>().configureEach {
    disableBuildCache.set(false)
  }
  val dokkaJekyll = named("dokkaJekyll", DokkaTask::class)
  val dokkaHtml = named("dokkaHtml", DokkaTask::class)
  named("jekyllMainAssemble", Copy::class) {
    into("gen/api") {
      from(dokkaJekyll)
    }
    from(rootDir.resolve("gen/CHANGELOG.md"))
  }
  jekyll.sourceSets.main.flatMap { it.serveTask }.get().incremental.set(true)
}
