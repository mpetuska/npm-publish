import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("convention.detekt")
  id("convention.base")
  kotlin("jvm")
}

tasks {
  withType<KotlinCompile> {
    kotlinOptions {
      languageVersion = "1.4"
      kotlinOptions {
        jvmTarget = "11"
      }
    }
  }
  withType<Test> {
    useJUnitPlatform()
  }
}
