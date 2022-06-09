plugins {
  id("io.github.gradle-nexus.publish-plugin")
  id("plugin.base")
  if (System.getenv("CI") in arrayOf(null, "0", "false", "n")) {
    id("plugin.git-hooks")
  }
}

gradleEnterprise {
  buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
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

println(
  """
  BUILD VERSIONS
  JDK: ${System.getProperty("java.version")}
  KOTLIN: $embeddedKotlinVersion
  Gradle: ${gradle.gradleVersion}
  """.trimIndent()
)
