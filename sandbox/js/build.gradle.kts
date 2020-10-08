plugins {
  kotlin("js") version "1.4.10"
  id("lt.petuska.npm.publish") version "0.0.4"
}

version = "1.0.0"
group = "test.group"


repositories {
  jcenter()
  mavenCentral()
}

kotlin {
  js { browser() }
  dependencies {
    implementation(npm("axios", "*"))
    api(devNpm("snabbdom", "*"))
  }
}

npmPublishing {
  repositories {
    repository("npmjs") {
      registry = uri("https://registry.npmjs.org")
      authToken = "asdhkjsdfjvhnsdrishdl"
    }
  }
}

