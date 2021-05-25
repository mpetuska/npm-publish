plugins {
  kotlin("multiplatform")
  id("dev.petuska.npm.publish")
  `maven-publish`
}

kotlin {
  js("browser", IR) {
    browser()
    useCommonJs()
    binaries.library()
  }
  js("node", IR) {
    useCommonJs()
    nodejs()
    binaries.library()
  }

  sourceSets {
    all {
      kotlin.srcDirs("src/main/kotlin")
      resources.srcDirs("src/main/resources")
      dependencies {
        implementation("io.ktor:ktor-client-core:1.4.1")
        implementation(devNpm("axios", "*"))
        api(npm("snabbdom", "*"))
      }
    }
    // val bothMain by getting
    // named("browserMain") {
    //   kotlin.srcDirs(bothMain.kotlin.srcDirs)
    //   resources.srcDirs(bothMain.resources.srcDirs)
    // }
    // named("nodeMain") {
    //   kotlin.srcDirs(bothMain.kotlin.srcDirs)
    //   resources.srcDirs(bothMain.resources.srcDirs)
    // }
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
    val browser by getting {
      moduleName = "mpp-browser"
      packageJson {
        // bundledDependencies = mutableSetOf("kotlin-test")
      }
    }
    val node by getting {
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
