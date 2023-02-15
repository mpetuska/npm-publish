import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("convention.detekt")
  id("convention.base")
  kotlin("jvm")
}

tasks {
  withType<JavaCompile> {
    targetCompatibility = "${JavaVersion.VERSION_11}"
  }
  withType<KotlinCompile> {
    kotlinOptions {
      languageVersion = embeddedKotlinVersion.split(".").take(2).joinToString(".")
      jvmTarget = "11"
    }
  }
  withType<Test> {
    useJUnitPlatform()
  }
}
