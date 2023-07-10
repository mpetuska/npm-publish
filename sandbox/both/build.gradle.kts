plugins {
  kotlin("multiplatform")
  id("convention.base")
  id("dev.petuska.npm.publish")
}

kotlin {
  js {
    nodejs()
    browser()
    useCommonJs()
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
  organization.set(group.toString())

  packages {
    named("js") {
      packageJsonTemplateFile.set(rootDir.resolve("template.package.json"))
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
