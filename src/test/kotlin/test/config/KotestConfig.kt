package dev.petuska.npm.publish.test.config

import io.kotest.core.config.*
import io.kotest.core.extensions.*
import io.kotest.core.test.*
import io.kotest.engine.test.logging.*

object KotestConfig : AbstractProjectConfig() {
  override val logLevel: LogLevel
    get() = System.getProperty("kotest.framework.loglevel")?.let(LogLevel::from) ?: LogLevel.Warn

  override fun extensions(): List<Extension> = listOf(
    object : LogExtension {
      override suspend fun handleLogs(testCase: TestCase, logs: List<LogEntry>) {
        logs
          .filter { it.level >= logLevel }
          .forEach { println(it.message) }
      }
    }
  )
}
