plugins {
  id("lt.petuska.npm.publish")
  kotlin("multiplatform")
  `maven-publish`
}

repositories {
  jcenter()
  mavenCentral()
}

kotlin {
  js { browser() }
  js("exe", IR) {
    browser()
    useCommonJs()
    binaries.executable()
  }

  sourceSets {
    val jsMain by getting {
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
      registry = uri("https://gitlab.com/api/v4/projects/${System.getenv("CI_PROJECT_ID")?.trim()}/packages/npm")
      authToken = System.getenv("PRIVATE_TOKEN")?.trim() ?: ""
    }
  }

  publications {
    val js by getting {
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
      }
    }
  }
}
