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
  js(IR) {
    browser()
    binaries.library()
  }
  js("app", IR) {
    browser()
    binaries.executable()
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
//      freeCompilerArgs += listOf("-Xir-per-module")
    }
  }
}

npmPublish {
  organization.set("$group")
  packages {
    named("js") {
      packageName.set("sandbox")
      dependencies {
        normal("axios", "*")
      }
    }
    register("custom") {
      main.set("custom.js")
//      packageJsonTemplateFile.set(rootDir.resolve("template.package.json"))
      packageName.set("custom")
//      packageJson {
//        author {
//          name ("Custom Author from DSL")
//        }
//        keywords("kotlin")
//        publishConfig {
//          tag = "latest"
//        }
//        "customField" by jsonObject {
//          "customValues" by jsonArray(1, 2, 3)
//        }
//        repository {
//          type = "git"
//          url = "https://github.com/mpetuska/npm-publish.git"
//        }
//      }
    }
  }
  registries {
    register("GitLab") {
      uri.set(uri("https://gitlab.com/api/v4/projects/${System.getenv("CI_PROJECT_ID")?.trim()}/packages/npm"))
      authToken.set(System.getenv("PRIVATE_TOKEN")?.trim() ?: "")
    }
    register("GitHub") {
      uri.set(uri("https://npm.pkg.github.com/"))
    }
  }
}
