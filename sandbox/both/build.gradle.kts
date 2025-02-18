plugins {
  id("kmp")
  id("dev.petuska.npm.publish")
}

kotlin {
  js {
    nodejs()
    browser()
    useCommonJs()
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
  organization = group.toString()

  packages {
    named("js") {
      packageJsonTemplateFile = rootDir.resolve("template.package.json")
      packageJson {
        author {
          name = "Martynas Petuška"
        }
        repository {
          type = "git"
          url = "https://github.com/mpetuska/npm-publish.git"
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
