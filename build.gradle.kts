import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version embeddedKotlinVersion
  id("plugin.common")
  id("plugin.publishing")
}
println("BUILD KOTLIN VERSION: $embeddedKotlinVersion")

description = """
  A maven-publish alternative for NPM package publishing.
  Integrates with kotlin JS/MPP plugins (if applied) to automatically
  setup publishing to NPM repositories for all JS targets.
""".trimIndent()

kotlin {
// TODO  explicitApi()
  dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:_")
    implementation("com.google.code.gson:gson:_")
    api("dev.petuska:kon:_")
    testImplementation("io.kotest:kotest-runner-junit5:_")
    testImplementation("dev.petuska:klip:_")
  }

  sourceSets.configureEach {
    languageSettings {
      optIn("io.kotest.common.ExperimentalKotest")
    }
  }
}

tasks {
  withType<KotlinCompile>().configureEach {
    kotlinOptions {
      jvmTarget = targetCompatibility
    }
  }
  withType<Test>().configureEach {
    systemProperty("kotest.framework.loglevel", "warn")
  }
}

gradleEnterprise {
  buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
  }
}
