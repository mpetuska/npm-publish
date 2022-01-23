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
    implementation("io.ktor:ktor-client-core:_")
  }
  sourceSets {
    all {
      languageSettings.optIn("kotlin.js.ExperimentalJsExport")
    }
  }
}

npmPublishing {
  organization = group as String

  publications {
    named("js") {
      packageJsonTemplateFile = projectDir.resolve("../template.package.json")
      packageJson {
        author { name = "Martynas Petuška" }
        repository {
          type = "git"
          url = "https://github.com/mpetuska/npm-publish.git"
        }
      }
    }
  }
  repositories {
    repository("GitHub") {
      registry = uri("https://npm.pkg.github.com/")
    }
  }
}
