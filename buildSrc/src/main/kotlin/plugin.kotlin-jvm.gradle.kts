import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("plugin.base")
  kotlin("jvm")
}

tasks {
  withType<KotlinCompile>().configureEach {
    kotlinOptions {
      jvmTarget = targetCompatibility
    }
  }
  withType<Test>().configureEach {
    useJUnitPlatform()
  }
}
