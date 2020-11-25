plugins {
  kotlin("js")
  id("lt.petuska.npm.publish")
}

repositories {
  jcenter()
  mavenCentral()
}

kotlin {
  js {
    browser()
  }
  dependencies {
    implementation(npm("axios", "*"))
    api(devNpm("snabbdom", "*"))
    implementation("io.ktor:ktor-client-core:1.4.1")
  }
  sourceSets {
  }
}

npmPublishing {
  organization = group as String

  publications {
    val js by getting {
      packageJson {
        author { name = "Martynas Petuška" }
      }
    }
  }
  repositories {
    repository("GitLab") {
      registry = uri("https://gitlab.com/api/v4/projects/${System.getenv("CI_PROJECT_ID")?.trim()}/packages/npm")
      authToken = System.getenv("PRIVATE_TOKEN")?.trim() ?: ""
    }
  }
}

