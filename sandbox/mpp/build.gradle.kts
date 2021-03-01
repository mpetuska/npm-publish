plugins {
  kotlin("multiplatform")
  id("lt.petuska.npm.publish")
  `maven-publish`
}

repositories {
  jcenter()
  mavenCentral()
}

kotlin {
  js { browser() }
  js("jsIR", IR) {
    browser()
    useCommonJs()
    binaries.library()
  }

  sourceSets {
    val jsMain by getting {
      dependencies {
        implementation("io.ktor:ktor-client-core:1.4.1")
        implementation(devNpm("axios", "*"))
        api(npm("snabbdom", "*"))
      }
    }
    val jsIRMain by getting {
      dependencies {
        implementation("io.ktor:ktor-client-core:1.4.1")
        implementation(devNpm("axios", "*"))
        api(npm("snabbdom", "*"))
      }
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
    val jsIR by getting {
      moduleName = "mpp-IR"
      packageJson {
        // bundledDependencies = mutableSetOf("kotlin-test")
      }
    }
    val js by getting {
      moduleName = "mpp-Legacy"
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
