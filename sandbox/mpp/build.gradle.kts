plugins {
  kotlin("multiplatform")
  id("convention.base")
  id("dev.petuska.npm.publish")
  `maven-publish`
}

kotlin {
  js {
    nodejs()
    useCommonJs()
    binaries.library()
  }

  sourceSets {
    named("jsMain") {
      dependencies {
        implementation(kotlin("test-js"))
        implementation(devNpm("is-odd", "*"))
        api(npm("is-even", "*"))
      }
    }
    all {
      languageSettings.optIn("kotlin.js.ExperimentalJsExport")
    }
  }
}

npmPublish {
  organization.set(group as String)

  packages {
    named("js") {
      packageName.set("mpp")
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
        "customField" by 1
        "customArray" by arrayOf(json {
          "innerField" by true
        })
        repository {
          type.set("git")
          url.set("https://github.com/mpetuska/npm-publish.git")
        }
      }
    }
  }
  registries {
    npmjs {
      dry.set(true)
    }
    github {
      dry.set(true)
    }
    register("custom") {
      uri.set(uri("https://registry.custom.com/"))
      dry.set(true)
    }
  }
}
