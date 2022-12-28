plugins {
  kotlin("js")
  id("convention.base")
  id("dev.petuska.npm.publish")
}

kotlin {
  js(IR) {
    browser()
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
          "is-odd" by "*"
          set("is-even", "*")
        }
        devDependencies {
          "is-whitespace" by "*"
          set("is-number", "*")
        }
        peerDependencies {
          "is-thirteen" by "*"
          set("left-pad", "*")
        }
        optionalDependencies {
          "is-array" by "*"
          set("is-object", "*")
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
