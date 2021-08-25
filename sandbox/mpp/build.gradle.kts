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
        implementation("io.ktor:ktor-client-core:_")
        implementation(devNpm("axios", "*"))
        api(npm("snabbdom", "*"))
      }
      languageSettings.optIn("kotlin.js.ExperimentalJsExport")
    }
  }
}

npmPublishing {
  organization = group as String
  repositories {
    repository("GitHub") {
      registry = uri("https://npm.pkg.github.com/@$group")
    }
  }

  publications {
    named("browser") {
      moduleName = "mpp-browser"
      packageJson {
        // bundledDependencies = mutableSetOf("kotlin-test")
      }
    }
    named("node") {
      moduleName = "mpp-node"
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
