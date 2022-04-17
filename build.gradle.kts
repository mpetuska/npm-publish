import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("plugin.common")
  id("plugin.docs")
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
    testImplementation("io.kotest:kotest-assertions-core:_")
    testImplementation("io.kotest:kotest-assertions-json:_")
    testImplementation("org.junit.jupiter:junit-jupiter-api:_")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:_")
  }
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

gradleEnterprise {
  buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
  }
}
