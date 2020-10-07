plugins {
  id("lt.petuska.kpm.publish") version "0.0.2"
  kotlin("js") version "1.4.10"
}

version = "1.0.0"
group = "test.group"


repositories {
  jcenter()
  mavenCentral()
}

kotlin {
  target {browser()}
  dependencies {
    implementation(npm("axios", "*"))
    api(npm("snabbdom", "*"))
  }
}
