plugins {
  id("com.gradle.plugin-publish")
  id("io.github.gradle-nexus.publish-plugin")
  `java-gradle-plugin`
  `maven-publish`
  signing
}

gradlePlugin {
  plugins {
    create(name) {
      id = "dev.petuska.npm.publish"
      displayName = "NPM package publishing to NPM repositories"
      description = rootProject.description
      implementationClass = "dev.petuska.npm.publish.NpmPublishPlugin"
    }
  }
}

pluginBundle {
  website = "https://github.com/mpetuska/${project.name}"
  vcsUrl = "https://github.com/mpetuska/${project.name}.git"
  tags = listOf("npm", "publishing", "kotlin", "node", "js")
}

publishing {
  publications {
    withType<MavenPublication> {
      pom {
        name by project.name
        url by "https://github.com/mpetuska/${project.name}"
        description by rootProject.description

        licenses {
          license {
            name by "The Apache License, Version 2.0"
            url by "https://www.apache.org/licenses/LICENSE-2.0.txt"
          }
        }

        developers {
          developer {
            id to "mpetuska"
            name to "Martynas Petuška"
            email to "martynas@petuska.dev"
          }
        }

        scm {
          connection by "scm:git:git@github.com:mpetuska/${project.name}.git"
          url by "https://github.com/mpetuska/${project.name}"
          tag by Git.headCommitHash
        }
      }
    }
    repositories {
      maven("https://maven.pkg.github.com/mpetuska/${project.name}") {
        name = "GitHub"
        credentials {
          username = System.getenv("GH_USERNAME")
          password = System.getenv("GH_PASSWORD")
        }
      }
    }
  }
}

java {
  withSourcesJar()
  withJavadocJar()
  targetCompatibility = JavaVersion.VERSION_11
}

tasks {
  withType<Test> {
    useJUnitPlatform()
  }
  withType<Jar> {
    manifest {
      attributes +=
        sortedMapOf(
          "Built-By" to System.getProperty("user.name"),
          "Build-Jdk" to System.getProperty("java.version"),
          "Build-Kotlin" to embeddedKotlinVersion,
          "Implementation-Version" to project.version,
          "Created-By" to GradleVersion.current(),
          "Created-From" to Git.headCommitHash
        )
    }
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

signing {
  val signingKey: String? by project
  val signingPassword: String? by project
  if (signingKey != null) {
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications)
  }
}
