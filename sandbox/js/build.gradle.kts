plugins {
  kotlin("js") version "1.4.10"
  id("lt.petuska.npm.publish") version "0.0.5"
}

version = "1.0.0"
group = "test.group"


repositories {
  jcenter()
  mavenCentral()
}

kotlin {
  js { browser() }
  dependencies {
    implementation(npm("axios", "*"))
    api(devNpm("snabbdom", "*"))
  }
}

npmPublishing {
  repositories {
    repository("GitLab") {
      registry = uri("https://gitlab.com/api/v4/projects/${System.getenv("CI_PROJECT_ID")?.trim()}/packages/npm")
      authToken = System.getenv("PRIVATE_TOKEN")?.trim() ?: ""
    }
  }
}

