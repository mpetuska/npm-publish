plugins {
  kotlin("multiplatform")
  id("kmp")
  id("dev.petuska.npm.publish")
}

kotlin {
  js {
    nodejs()
    useCommonJs()
    generateTypeScriptDefinitions()
    binaries.library()
  }
  sourceSets {
    named("jsMain") {
      dependencies {
        api(npm("is-even", "*"))
        implementation(devNpm("is-odd", "*"))
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
      packageJsonTemplateFile = projectDir.resolve("../template.package.json")
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
