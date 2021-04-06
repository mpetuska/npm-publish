import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.3.72"
  `java-gradle-plugin`
  `maven-publish`
  id("com.gradle.plugin-publish")
  id("org.jetbrains.dokka")
  id("com.github.jakemarsden.git-hooks")
  id("org.jlleitschuh.gradle.ktlint")
  idea
}

description = "Gradle plugin for npm package publishing"

idea {
  module {
    isDownloadJavadoc = true
    isDownloadSources = true
  }
}

gitHooks {
  setHooks(
    mapOf(
      "post-checkout" to "ktlintApplyToIdea",
      "pre-commit" to "ktlintFormat",
      "pre-push" to "check"
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
    api("org.jetbrains.kotlin:kotlin-gradle-plugin:_")
    testImplementation("io.kotest:kotest-runner-junit5:_")
  }
  target.compilations {
    val main by getting
    create("functionalTest") {
      dependencies {
      }
      associateWith(main)
      val functionalTest by tasks.register<Test>("functionalTest") {
        group = JavaBasePlugin.VERIFICATION_GROUP
        description = "Runs functional tests"
        testClassesDirs = output.classesDirs
        classpath = project.sourceSets["functionalTest"].runtimeClasspath
      }
      tasks.named("check") {
        dependsOn(functionalTest)
      }
    }
  }
  configurations.getByName("functionalTestImplementation") {
    extendsFrom(configurations.getByName("implementation"))
    extendsFrom(configurations.getByName("testImplementation"))
  }

  configurations.getByName("functionalTestRuntimeOnly") {
    extendsFrom(configurations.getByName("runtimeOnly"))
    extendsFrom(configurations.getByName("testRuntimeOnly"))
  }
}

val pluginId = "lt.petuska.npm.publish"
gradlePlugin {
  plugins {
    create(project.name) {
      id = pluginId
      displayName = "NPM package publishing to NPM repositories"
      description =
        """
              A maven-publish alternative for NPM package publishing.
              Integrates with kotlin JS/MPP plugins (if applied) to automatically
              setup publishing to NPM repositories for all JS targets.
        """.trimIndent()
      implementationClass = "lt.petuska.npm.publish.NpmPublishPlugin"
    }
  }
}

pluginBundle {
  website = "https://github.com/mpetuska/${project.name}"
  vcsUrl = "https://github.com/mpetuska/${project.name}.git"
  tags = listOf("npm", "publishing", "kotlin", "node")
}

tasks {
  withType<KotlinCompile> {
    kotlinOptions {
      jvmTarget = "1.8"
    }
  }
}

publishing {
  fun checkAnyTrue(vararg props: String) = props.any {
    "true".equals(project.properties[it] as String?, true)
  }

  fun checkNoneStarting(vararg props: String) = props.none {
    project.properties.keys.any { p -> p.startsWith(it) }
  }
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

        scm {
          connection by "scm:git:git@github.com:mpetuska/${project.name}.git"
          url by "https://github.com/mpetuska/${project.name}"
          tag by Git.headCommitHash
        }
      }
    }
    repositories {
      fun repository(name: String, config: MavenArtifactRepository.() -> Unit) {
        if ((checkAnyTrue("publish.all", "publish.$name") && checkNoneStarting("publish.skip")) &&
          checkNoneStarting("publish.skip.$name")
        ) {
          maven {
            this.name = name
            config()
          }
        }
      }
      repository("GitHub") {
        url = uri("https://maven.pkg.github.com/mpetuska/${project.name}")
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
          "Created-By" to "Gradle v${org.gradle.util.GradleVersion.current()}",
          "Created-From" to Git.headCommitHash
        )
      }
    }
    withType<Test> {
      useJUnitPlatform()
    }
  }
}
