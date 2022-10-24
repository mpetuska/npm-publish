plugins {
  kotlin("js")
  id("dev.petuska.npm.publish")
}

kotlin {
  js(IR) {
    browser()
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
      packageJsonTemplateFile.set(rootDir.resolve("template.package.json"))
      packageJson {
        author { name.set("Martynas Petuška") }
        repository {
          type.set("git")
          url.set("https://github.com/mpetuska/npm-publish.git")
        }

        dependencies {
          "dep-plain-one" by "*"
          set("dep-plain-two", "*")
        }
        devDependencies {
          "dev-dep-one" by "*"
          set("dev-dep-two", "*")
        }
        peerDependencies {
          "peer-dep-one" by "*"
          set("peer-dep-two", "*")
        }
        optionalDependencies {
          "opt-dep-one" by "*"
          set("opt-dep-two", "*")
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
