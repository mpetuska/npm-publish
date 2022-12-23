plugins {
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
    implementation(Ktor.client.core)
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
