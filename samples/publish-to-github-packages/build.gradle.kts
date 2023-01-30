plugins {
  id("convention.base")
  kotlin("js")
  id("dev.petuska.npm.publish")
}

kotlin {
  js {
    nodejs()
    useCommonJs()
    binaries.library()
  }
  dependencies {
    implementation(npm("is-odd", "*"))
    api(devNpm("is-even", "*"))
    implementation(kotlin("test-js"))
  }
  sourceSets {
    all {
      languageSettings.optIn("kotlin.js.ExperimentalJsExport")
    }
  }
}

npmPublish {
  organization.set("mpetuska")
  packages {
    named("js") {
      packageJson {
        author {
          name.set("Martynas Petuška")
        }
        repository {
          type.set("git")
          url.set("https://github.com/mpetuska/npm-publish.git")
        }
      }
    }
  }
  registries {
    github {}
  }
}
