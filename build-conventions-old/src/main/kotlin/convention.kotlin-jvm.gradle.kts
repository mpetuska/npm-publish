import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("convention.detekt")
  id("convention.base")
  kotlin("jvm")
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get().toInt()))
  }
}

dependencies {
  testImplementation(libs.bundles.kotest.assertions)
  testImplementation(libs.junit.jupiter.api)
  testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks {
  withType<KotlinCompile> {
    compilerOptions {
      languageVersion by embeddedKotlinVersion.split(".").take(2)
        .joinToString(".").let(KotlinVersion::fromVersion)
    }
  }
  withType<Test> {
    useJUnitPlatform()
  }
}
