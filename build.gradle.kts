plugins {
  alias(libs.plugins.nexus.publish)
  id("convention.base")
  id("convention.versions")
  id("convention.git-hooks")
}

nexusPublishing.repositories {
  sonatype {
    nexusUrl by uri("https://s01.oss.sonatype.org/service/local/")
    snapshotRepositoryUrl by uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
  }
}

gradleEnterprise {
  buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
  }
}
