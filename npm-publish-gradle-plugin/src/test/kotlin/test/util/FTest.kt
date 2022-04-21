package dev.petuska.npm.publish.test.util

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.io.TempDir
import org.junit.platform.commons.logging.Logger
import org.junit.platform.commons.logging.LoggerFactory
import java.io.File
import java.io.Writer

@Tags(Tag("functional"))
abstract class FTest {
  protected val logger: Logger by lazy { LoggerFactory.getLogger(this::class.java) }

  @TempDir
  private lateinit var tempDir: File
  protected fun executeBuild(
    vararg arguments: String,
    init: (projectDir: File) -> Unit = {},
    buildFile: BuildFileBuilder.() -> Unit
  ): BuildResult {
    val buildDir = tempDir
    val buildF = buildDir.resolve("build.gradle.kts")
    val buildFC = BuildFileBuilder(buildFile).toString()
    buildF.writeText(buildFC)
    init(buildDir)
    logger.debug { "=============== BUILD FILE =============================================>" }
    logger.debug { buildFC }
    logger.debug { "=============== BUILD OUTPUT =============================================>" }
    val stdOut = LogWriter { logger.info(it) }
    val errOut = LogWriter { logger.error(it) }
    val result = kotlin.runCatching {
      GradleRunner.create().forwardStdOutput(stdOut).forwardStdError(errOut).withPluginClasspath()
        .withProjectDir(buildDir).withArguments(listOf("--console=plain") + arguments)
        .forwardStdError(Writer.nullWriter()).build()
    }
    stdOut.close()
    errOut.close()
    return result.getOrThrow()
  }

  private class LogWriter(private val log: (() -> String) -> Unit) : Writer() {
    private val buffer = StringBuffer()

    override fun close() {
      log { buffer.toString() }
      buffer.delete(0, buffer.length)
    }

    override fun flush() = Unit

    override fun write(cbuf: CharArray, off: Int, len: Int) {
      buffer.append(cbuf, off, len)
    }
  }
}
