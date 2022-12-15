allprojects {
  repositories {
    mavenLocal()
    mavenCentral()
    google()
    gradlePluginPortal()
  }
}

gradleEnterprise {
  buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
  }
}
