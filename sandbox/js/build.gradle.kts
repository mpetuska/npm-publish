plugins {
  kotlin("js")
  id("dev.petuska.npm.publish")
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
      packageJsonTemplateFile = projectDir.resolve("../template.package.json")
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

