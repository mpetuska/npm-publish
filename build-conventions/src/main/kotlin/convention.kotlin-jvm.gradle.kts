import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("convention.detekt")
  id("convention.base")
  kotlin("jvm")
}

dependencies {
  testImplementation(libs.bundles.kotest.assertions)
  testImplementation(libs.junit.jupiter.api)
  testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks {
  withType<JavaCompile> {
    targetCompatibility = libs.versions.java.get()
  }
  withType<KotlinCompile> {
    compilerOptions {
      languageVersion by embeddedKotlinVersion.split(".").take(2)
        .joinToString(".").let(KotlinVersion::fromVersion)
      jvmTarget by JvmTarget.fromTarget(libs.versions.java.get())
    }
  }
  withType<Test> {
    useJUnitPlatform()
  }
}
