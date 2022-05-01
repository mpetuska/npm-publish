import de.fayard.refreshVersions.core.versionFor

println(
  """
TOOL VERSIONS
  JDK: ${System.getProperty("java.version")}
  KOTLIN: ${versionFor("version.kotlin")}
  Gradle: ${gradle.gradleVersion}
""".trimIndent()
)

allprojects {
  repositories {
    mavenLocal()
    mavenCentral()
    google()
  }
}
