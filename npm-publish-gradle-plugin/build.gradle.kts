@file:Suppress("UnstableApiUsage")

plugins {
  id("kjvm")
  id("detekt")
  id("dokkatoo")
  id("full-publishing")
  alias(libs.plugins.plugin.publish)
}

description = """
  A maven-publish alternative for NPM package publishing.
  Integrates with kotlin JS/MPP plugins (if applied) to automatically
  setup publishing to NPM repositories for all JS targets.
""".trimIndent()

repositories {
  mavenCentral()
  gradlePluginPortal()
}

kotlin {
  dependencies {
    compileOnly(libs.plugin.kotlin)
    compileOnly(libs.plugin.node.gradle)

    testImplementation(libs.plugin.kotlin)
    testImplementation(libs.bundles.kotest.assertions)
  }
}

gradlePlugin {
  website = "https://npm-publish.petuska.dev"
  vcsUrl = "https://github.com/mpetuska/npm-publish"
  plugins {
    create(name) {
      id = "dev.petuska.npm.publish"
      implementationClass = "dev.petuska.npm.publish.NpmPublishPlugin"
      displayName = "NPM package publishing to NPM repositories"
      description = project.description
      tags = listOf("npm", "publishing", "kotlin", "node", "js")
    }
  }
}

deployer {
  content {
    gradlePluginComponents()
  }
}

tasks {
  register<Jar>("javadocJar") {
    from(dokkatooGeneratePublicationHtml)
    archiveClassifier = "javadoc"
  }
  whenTaskAdded {
    if (name.contains("CentralPortal", ignoreCase = true) || name.contains("Github", ignoreCase = true)) {
      dependsOn("javadocJar", "sourcesJar", "jar", "makeEmptyDocsJar", "makeEmptySourcesJar")
    }
  }
}
