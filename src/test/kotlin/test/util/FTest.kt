package dev.petuska.npm.publish.test.util

import io.kotest.core.*
import io.kotest.core.spec.style.*
import io.kotest.core.spec.style.scopes.*
import io.kotest.engine.spec.*
import io.kotest.engine.test.logging.*
import org.gradle.testkit.runner.*
import java.io.*

abstract class FTest : StringSpec() {
  init {
    tags(Tag("functional"))
  }

  protected fun StringSpecScope.executeBuild(
    vararg arguments: String,
    init: (projectDir: File) -> Unit = {},
    buildFile: BuildFileBuilder.() -> Unit
  ): BuildResult {
    val buildDir = tempdir()
    val buildF = buildDir.resolve("build.gradle.kts")
    val buildFC = BuildFileBuilder(buildFile).toString()
    buildF.writeText(buildFC)
    init(buildDir)
    debug { "=============== BUILD FILE =============================================>" }
    debug { buildFC }
    debug { "=============== BUILD OUTPUT =============================================>" }
    val stdOut = LogWriter { info { it } }
    val errOut = LogWriter { error { it } }
    val result = kotlin.runCatching {
      GradleRunner.create()
        .forwardStdOutput(stdOut)
        .forwardStdError(errOut)
        .withPluginClasspath()
        .withProjectDir(buildDir)
        .withArguments(listOf("--console=plain") + arguments)
        .forwardStdError(Writer.nullWriter())
        .build()
    }
    stdOut.close()
    errOut.close()
    return result.getOrThrow()
  }

  private class LogWriter(private val log: (String) -> Unit) : Writer() {
    private val buffer = StringBuffer()

    override fun close() {
      log(buffer.toString())
      buffer.delete(0, buffer.length)
    }

    override fun flush() = Unit

    override fun write(cbuf: CharArray, off: Int, len: Int) {
      buffer.append(cbuf, off, len)
    }
  }
}
