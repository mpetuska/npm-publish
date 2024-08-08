plugins {
  alias(libs.plugins.nexus.publish)
  id("versions")
  id("detekt")
}

nexusPublishing {
  repositories {
    sonatype {
      nexusUrl = uri("https://s01.oss.sonatype.org/service/local/")
      snapshotRepositoryUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
  }
}
