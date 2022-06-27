import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("plugin.base")
  kotlin("jvm")
}

tasks {
  withType<KotlinCompile>().configureEach {
    kotlinOptions {
      languageVersion = "1.4"
    }
  }
  withType<Test>().configureEach {
    useJUnitPlatform()
  }
}
