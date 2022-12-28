plugins {
  id("io.github.gradle-nexus.publish-plugin")
  id("convention.base")
  if (System.getenv("CI") in arrayOf(null, "0", "false", "n")) {
    id("convention.git-hooks")
  }
}

nexusPublishing {
  repositories {
    sonatype {
      nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
      snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
    }
  }
}

gradleEnterprise {
  buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
  }
}

println(
  """
  BUILD VERSIONS
  JDK: ${System.getProperty("java.version")}
  KOTLIN: $embeddedKotlinVersion
  Gradle: ${gradle.gradleVersion}
  """.trimIndent()
)
