import dev.petuska.npm.publish.extension.domain.json.ExportedPath

plugins {
  kotlin("multiplatform")
  id("dev.petuska.npm.publish")
}

kotlin {
  js(IR) {
    nodejs()
    generateTypeScriptDefinitions()
    binaries.library()
  }
  sourceSets {
    named("jsMain") {
      dependencies {
        implementation(npm("is-even", "*"))
      }
    }
  }
}

npmPublish {
  organization.set("mpetuska")
  packages {
    named("js") {
      packageJson {
        exports by "index.mjs"
      }
    }
  }
}