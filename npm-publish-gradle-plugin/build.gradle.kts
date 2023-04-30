@file:Suppress("UnstableApiUsage")

plugins {
  id("convention.kotlin-jvm")
  alias(libs.plugins.plugin.publish)
  alias(libs.plugins.dokka)
  signing
}

description = """
  A maven-publish alternative for NPM package publishing.
  Integrates with kotlin JS/MPP plugins (if applied) to automatically
  setup publishing to NPM repositories for all JS targets.
""".trimIndent()

kotlin {
  explicitApi()
  dependencies {
    compileOnly(libs.plugin.kotlin)
    compileOnly(libs.plugin.nebula.node)

    testImplementation(libs.plugin.kotlin)
  }
}

gradlePlugin {
  website by "https://npm-publish.petuska.dev"
  vcsUrl by "https://github.com/mpetuska/npm-publish"
  plugins {
    register(name) {
      id = "dev.petuska.npm.publish"
      implementationClass = "dev.petuska.npm.publish.NpmPublishPlugin"
      displayName = "NPM package publishing to NPM repositories"
      description = project.description
      tags.set(listOf("npm", "publishing", "kotlin", "node", "js"))
    }
  }
}

publishing {
  publications {
    withType<MavenPublication> {
      pom {
        name by project.name
        url by gradlePlugin.website
        description by provider { project.description }

        licenses {
          license {
            name by "The Apache License, Version 2.0"
            url by "https://www.apache.org/licenses/LICENSE-2.0.txt"
          }
        }

        developers {
          developer {
            id by "mpetuska"
            name by "Martynas Petuška"
            email by "martynas@petuska.dev"
          }
        }

        scm {
          connection by "scm:git:git://github.com/mpetuska/npm-publish.git"
          developerConnection by "scm:git:git@github.com:mpetuska/npm-publish.git"
          url by gradlePlugin.vcsUrl
          tag by provider { Git.headCommitHash }
        }
      }
    }
    repositories {
      maven("https://maven.pkg.github.com/mpetuska/npm-publish") {
        name = "GitHub"
        credentials {
          username = System.getenv("GH_USERNAME")
          password = System.getenv("GH_PASSWORD")
        }
      }
    }
  }
}
