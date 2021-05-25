plugins {
  kotlin("multiplatform") version "1.5.10" apply false
  id("dev.petuska.npm.publish") version "0.0.0"
}

allprojects {
  group = "dev.petuska"
  version = "0.0.0"

  repositories {
    mavenLocal()
    mavenCentral()
  }
}

npmPublishing {
  organization = group as String
  publications {
    publication("custom") {
      nodeJsDir = file("${System.getProperty("user.home")}/.gradle/nodejs/node-v12.16.1-linux-x64")
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
  repositories {
    repository("GitLab") {
      registry = uri("https://gitlab.com/api/v4/projects/${System.getenv("CI_PROJECT_ID")?.trim()}/packages/npm")
      authToken = System.getenv("PRIVATE_TOKEN")?.trim() ?: ""
    }
  }
}

