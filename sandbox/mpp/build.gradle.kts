plugins {
  kotlin("multiplatform")
  id("dev.petuska.npm.publish")
  `maven-publish`
}

kotlin {
  js("browser") {
    browser()
    useCommonJs()
    binaries.library()
  }
  js("node") {
    useCommonJs()
    nodejs()
    binaries.library()
  }

  sourceSets {
    all {
      dependencies {
        implementation("io.ktor:ktor-client-core:_")
        implementation(devNpm("axios", "*"))
        api(npm("snabbdom", "*"))
      }
      languageSettings.useExperimentalAnnotation("kotlin.js.ExperimentalJsExport")
    }
  }
}

npmPublishing {
  organization = group as String
  repositories {
    repository("GitLab") {
      access = PUBLIC
      registry = uri("https://gitlab.com/api/v4/projects/${System.getenv("CI_PROJECT_ID")?.trim()}/packages/npm")
      authToken = System.getenv("PRIVATE_TOKEN")?.trim() ?: ""
    }
  }

  publications {
    named("browser") {
      moduleName = "mpp-browser"
      packageJson {
        // bundledDependencies = mutableSetOf("kotlin-test")
      }
    }
    named("node") {
      moduleName = "mpp-node"
      packageJson {
        author to "Custom Author"
        keywords = jsonArray(
          "kotlin"
        )
        publishConfig {
          tag = "latest"
        }
        "customField" to jsonObject {
          "customValues" to jsonArray(1, 2, 3)
        }
        // bundledDependencies("kotlin-test") {
        //
        // }
      }
    }
  }
}
