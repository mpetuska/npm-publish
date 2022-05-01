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
      }
    }
  }
  registries {
    register("GitHub") {
      uri.set("https://npm.pkg.github.com/")
    }
  }
}
