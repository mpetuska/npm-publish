plugins {
  kotlin("js")
  id("dev.petuska.npm.publish")
}

kotlin {
  js {
    browser()
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
      }
    }
  }
  repositories {
    repository("GitHub") {
      registry = uri("https://npm.pkg.github.com/@$group")
    }
  }
}

