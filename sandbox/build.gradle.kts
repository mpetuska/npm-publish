import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile

plugins {
  kotlin("multiplatform")
  id("dev.petuska.npm.publish")
}

allprojects {
  repositories {
    mavenLocal()
    mavenCentral()
    google()
  }
}

kotlin {
  js {
    browser()
    binaries.library()
  }

  sourceSets {
    all {
      languageSettings.optIn("kotlin.js.ExperimentalJsExport")
    }
    named("jsMain") {
      dependencies {
        api(project(":both"))
      }
    }
  }
}

tasks {
  named("compileProductionLibraryKotlinJs", KotlinJsCompile::class.java) {
    kotlinOptions {
//      sourceMap = true
//      sourceMapEmbedSources = "always"
      freeCompilerArgs += listOf("-Xir-per-module")
    }
  }
}

npmPublishing {
  organization = "$group"
  publications {
    named("js") {
      moduleName = "sandbox"
    }
    publication("custom") {
      nodeJsDir = file("${System.getProperty("user.home")}/.gradle/nodejs").listFiles()
        ?.find { it.isDirectory && it.name.startsWith("node") }
      packageJsonTemplateFile = rootDir.resolve("template.package.json")
      moduleName = "custom"
      packageJson {
        author {
          name = "Custom Author from DSL"
        }
        keywords = jsonArray(
          "kotlin"
        )
        publishConfig {
          tag = "latest"
        }
        "customField" to jsonObject {
          "customValues" to jsonArray(1, 2, 3)
        }
        repository {
          type = "git"
          url = "https://github.com/mpetuska/npm-publish.git"
        }
      }
    }
  }
  repositories {
    repository("GitLab") {
      registry = uri("https://gitlab.com/api/v4/projects/${System.getenv("CI_PROJECT_ID")?.trim()}/packages/npm")
      authToken = System.getenv("PRIVATE_TOKEN")?.trim() ?: ""
    }
    repository("GitHub") {
      registry = uri("https://npm.pkg.github.com/")
    }
  }
}
