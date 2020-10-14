plugins {
  id("lt.petuska.npm.publish") version "0.1.1"
  kotlin("multiplatform") version "1.4.10"
}

allprojects {
  group = "lt.petuska"
  version = "0.1.1"

  repositories {
    jcenter()
    mavenCentral()
  }
}

kotlin {
  js { browser() }
  jvm()

  sourceSets {
    val jsMain by getting {
      dependencies {
        implementation(npm("axios", "*"))
        api(peerNpm("snabbdom", "*"))
        api(optionalNpm("react", "*"))
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
}

