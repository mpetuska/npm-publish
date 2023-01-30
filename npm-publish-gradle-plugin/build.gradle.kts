plugins {
  id("convention.kotlin-jvm")
  id("convention.publishing")
}

description = """
  A maven-publish alternative for NPM package publishing.
  Integrates with kotlin JS/MPP plugins (if applied) to automatically
  setup publishing to NPM repositories for all JS targets.
""".trimIndent()

kotlin {
  explicitApi()
  dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:_")
    testImplementation("org.jetbrains.kotlin:kotlin-gradle-plugin:_")
    testImplementation(Testing.kotest.assertions.core)
    testImplementation(Testing.kotest.assertions.json)
    testImplementation(Testing.junit.jupiter.api)
    testRuntimeOnly(Testing.junit.jupiter.engine)
  }
}
