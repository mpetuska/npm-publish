plugins {
  `kotlin-dsl`
}

dependencies {
  implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

  implementation(kotlin("gradle-plugin", embeddedKotlinVersion))
  implementation(libs.plugin.git.hooks)
  implementation(libs.plugin.detekt)
  implementation(libs.plugin.versions)
  implementation(libs.plugin.versions.update)
  implementation(libs.plugin.container.tasks)
}

gradleEnterprise {
  buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
  }
}
