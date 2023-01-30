plugins {
  kotlin("js")
  id("convention.base")
  id("dev.petuska.npm.publish")
}

dependencies {
  implementation(npm("is-odd", "*"))
  api(devNpm("is-even", "*"))
  implementation(kotlin("test-js"))
}

kotlin {
  js {
    nodejs()
    useCommonJs()
    binaries.library()
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
    github {
      dry.set(true)
    }
    register("custom") {
      uri.set(uri("https://registry.custom.com/"))
      dry.set(true)
    }
  }
}
