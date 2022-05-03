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
    implementation(npm("axios", "*"))
    api(devNpm("snabbdom", "*"))
    implementation(Ktor.client.core)
  }
  sourceSets {
    all {
      languageSettings.optIn("kotlin.js.ExperimentalJsExport")
    }
  }
}

npmPublish {
  organization.set(group as String)
  packages {
    named("js") {
      packageJsonTemplateFile.set(projectDir.resolve("../template.package.json"))
      packageJson {
        author { name.set("Martynas Petuška") }
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
    gitHub {
      dry.set(true)
    }
    register("custom") {
      uri.set(uri("https://registry.custom.com/"))
      dry.set(true)
    }
  }
}
