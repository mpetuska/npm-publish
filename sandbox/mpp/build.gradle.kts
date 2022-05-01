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
        implementation(Ktor.client.core)
        implementation(devNpm("axios", "*"))
        api(npm("snabbdom", "*"))
      }
      languageSettings.optIn("kotlin.js.ExperimentalJsExport")
    }
  }
}

npmPublish {
  organization.set(group as String)
  registries {
    register("GitHub") {
      uri.set("https://npm.pkg.github.com/")
    }
  }

  packages {
    named("browser") {
      packageName.set("mpp-browser")
      packageJson {
        repository {
          type.set("git")
          url.set("https://github.com/mpetuska/npm-publish.git")
        }
      }
    }
    named("node") {
      packageName.set("mpp-node")
      packageJson {
        author {
          name.set("Custom Author")
        }
        keywords.addAll(
          "kotlin"
        )
        publishConfig {
          tag.set("latest")
        }
        "customObject" by {
          "customValues" by arrayOf(1, 2, 3)
        }
        repository {
          type.set("git")
          url.set("https://github.com/mpetuska/npm-publish.git")
        }
      }
    }
  }
}
