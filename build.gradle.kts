import de.fayard.refreshVersions.core.versionFor
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.4.31"
  `java-gradle-plugin`
  `maven-publish`
  id("com.gradle.plugin-publish")
  id("org.jetbrains.dokka")
  id("com.github.jakemarsden.git-hooks")
  id("org.jlleitschuh.gradle.ktlint")
  id("io.github.gradle-nexus.publish-plugin")
  signing
  idea
}

description = """
              A maven-publish alternative for NPM package publishing.
              Integrates with kotlin JS/MPP plugins (if applied) to automatically
              setup publishing to NPM repositories for all JS targets.
""".trimIndent()

idea {
  module {
    isDownloadJavadoc = true
    isDownloadSources = true
  }
}

ktlint {
  version by versionFor("version.ktlint")
  additionalEditorconfigFile.set(rootDir.resolve(".editorconfig"))
}

gradleEnterprise {
  buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
  }
}

gitHooks {
  setHooks(
    mapOf(
      "pre-commit" to "ktlintFormat",
      "pre-push" to "ktlintCheck"
    )
  )
}

repositories {
  mavenLocal()
  mavenCentral()
  gradlePluginPortal()
}

kotlin {
  dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:_")
    implementation("com.google.code.gson:gson:_")
    testImplementation("io.kotest:kotest-runner-junit5:_")
  }
}

val pluginId = "dev.petuska.npm.publish"
gradlePlugin {
  plugins {
    create(project.name) {
      id = pluginId
      displayName = "NPM package publishing to NPM repositories"
      description = rootProject.description
      implementationClass = "dev.petuska.npm.publish.NpmPublishPlugin"
    }
  }
}

pluginBundle {
  website = "https://github.com/mpetuska/${project.name}"
  vcsUrl = "https://github.com/mpetuska/${project.name}.git"
  tags = listOf("npm", "publishing", "kotlin", "node")
}

java {
  withSourcesJar()
  withJavadocJar()
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

nexusPublishing {
  repositories {
    sonatype {
      nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
      snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
    }
  }
}

signing {
  val signingKey: String? by project
  val signingPassword: String? by project
  if (signingKey != null) {
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications)
  }
}

tasks {
  withType<KotlinCompile> {
    kotlinOptions {
      jvmTarget = "${JavaVersion.VERSION_11}"
    }
  }
}

publishing {
  publications {
    withType<MavenPublication> {
      pom {
        name by project.name
        url by "https://github.com/mpetuska/${project.name}"
        description by rootProject.description

        licenses {
          license {
            name by "The Apache License, Version 2.0"
            url by "https://www.apache.org/licenses/LICENSE-2.0.txt"
          }
        }

        developers {
          developer {
            id to "mpetuska"
            name to "Martynas Petuška"
            email to "martynas@petuska.dev"
          }
        }

        scm {
          connection by "scm:git:git@github.com:mpetuska/${project.name}.git"
          url by "https://github.com/mpetuska/${project.name}"
          tag by Git.headCommitHash
        }
      }
    }
    repositories {
      maven("https://maven.pkg.github.com/mpetuska/${project.name}") {
        name = "GitHub"
        credentials {
          username = System.getenv("GH_USERNAME")
          password = System.getenv("GH_PASSWORD")
        }
      }
    }
  }
}

afterEvaluate {
  tasks {
    withType<Jar> {
      manifest {
        attributes += sortedMapOf(
          "Built-By" to System.getProperty("user.name"),
          "Build-Jdk" to System.getProperty("java.version"),
          "Implementation-Version" to project.version,
          "Created-By" to "Gradle v${GradleVersion.current()}",
          "Created-From" to Git.headCommitHash
        )
      }
    }
    withType<Test> {
      useJUnitPlatform()
    }
  }
}
