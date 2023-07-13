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
}