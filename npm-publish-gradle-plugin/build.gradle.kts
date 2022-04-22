plugins {
  id("plugin.kotlin-jvm")
  id("plugin.publishing")
}

description = """
  A maven-publish alternative for NPM package publishing.
  Integrates with kotlin JS/MPP plugins (if applied) to automatically
  setup publishing to NPM repositories for all JS targets.
""".trimIndent()

kotlin {
  explicitApi()
  dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:_")
    implementation("com.google.code.gson:gson:_")
    testImplementation(Testing.kotest.assertions.core)
    testImplementation(Testing.kotest.assertions.json)
    testImplementation(Testing.junit.jupiter.api)
    testRuntimeOnly(Testing.junit.jupiter.engine)
  }
}

println("BUILD KOTLIN VERSION: $embeddedKotlinVersion")
