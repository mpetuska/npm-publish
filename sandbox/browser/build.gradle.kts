plugins {
  id("kmp")
  id("dev.petuska.npm.publish")
}

kotlin {
  js(IR) {
    browser()
    useEsModules()
    generateTypeScriptDefinitions()
    binaries.library()
  }
  sourceSets {
    named("jsMain") {
      dependencies {
        implementation(npm("is-odd", "*"))
        api(devNpm("is-even", "*"))
        implementation(kotlin("test-js"))
      }
    }
    all {
      languageSettings.optIn("kotlin.js.ExperimentalJsExport")
    }
  }
}

npmPublish {
  organization = group as String

  packages {
    named("js") {
      packageJsonTemplateFile = rootDir.resolve("template.package.json")
      packageJson {
        author {
          name = "Martynas Petuška" }
        repository {
          type = "git"
          url = "https://github.com/mpetuska/npm-publish.git"
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
      dry = true
    }
    github {
      dry = true
    }
    register("custom") {
      uri = uri("https://registry.custom.com/")
      dry = true
    }
  }
}
