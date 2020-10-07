package lt.petuska.kpm.publish.util

import org.gradle.testkit.runner.*
import java.io.*

fun buildGradleFile(
  kotlinPlugin: String,
  kotlinBlock: String,
  suffix: StringBuilder.() -> Unit = {}
) = """
plugins {
  id("lt.petuska.kpm.publish")
  ${if (kotlinPlugin.isNotEmpty()) "kotlin(\"$kotlinPlugin\")" else ""}
}

version = "1.0.0"
group = "test.group"


repositories {
  jcenter()
  mavenCentral()
}

$kotlinBlock
${buildString(suffix)}
""".trimIndent()

fun File.gradleExec(buildFile: String, vararg args: String): BuildResult {
  deleteRecursively()
  mkdirs()
  resolve("settings.gradle.kts").writeText("rootProject.name = \"test-project\"")
  resolve("build.gradle.kts").writeText(buildFile)
  
  return GradleRunner.create()
    .forwardOutput()
    .withPluginClasspath()
    .withProjectDir(this)
    .withArguments(*args)
    .build()
}
