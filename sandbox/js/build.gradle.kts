plugins {
  kotlin("js")
  id("lt.petuska.npm.publish")
}

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
  organization = group as String
  repositories {
    repository("GitLab") {
      registry = uri("https://gitlab.com/api/v4/projects/${System.getenv("CI_PROJECT_ID")?.trim()}/packages/npm")
      authToken = System.getenv("PRIVATE_TOKEN")?.trim() ?: ""
    }
  }
}

