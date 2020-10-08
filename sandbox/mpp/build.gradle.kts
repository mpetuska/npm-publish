plugins {
  id("lt.petuska.npm.publish") version "0.0.4"
  kotlin("multiplatform") version "1.4.10"
}

version = "1.0.0"
group = "test.group"


repositories {
  jcenter()
  mavenCentral()
}

kotlin {
  js { browser() }

  sourceSets {
    val jsMain by getting {
      dependencies {
        implementation(npm("axios", "*"))
        api(npm("snabbdom", "*"))
      }
    }
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
