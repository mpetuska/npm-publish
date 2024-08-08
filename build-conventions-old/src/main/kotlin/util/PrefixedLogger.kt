package util

import org.gradle.api.tasks.Internal
import org.slf4j.Logger
import org.slf4j.Marker
import org.slf4j.MarkerFactory

/**
 * Standardised logging utilities with the plugin name prefix
 */
interface PrefixedLogger {
  @get:Internal
  val prefix: String

  @get:Internal
  val marker: Marker get() = MarkerFactory.getMarker(prefix)

  @Internal
    /**
     * Logger provider
     * @return logger to use for all logging events
     */
  fun getLogger(): Logger

  /**
   * Logs at error level
   * @param message provider
   */
  fun error(message: () -> String) {
    if (getLogger().isErrorEnabled) {
      getLogger().error(marker, "[$prefix] ${message()}")
    }
  }

  /**
   * Logs at warn level
   * @param message provider
   */
  fun warn(message: () -> String) {
    if (getLogger().isWarnEnabled) {
      getLogger().warn(marker, "[$prefix] ${message()}")
    }
  }

  /**
   * Logs at info level
   * @param message provider
   */
  fun info(message: () -> String) {
    if (getLogger().isInfoEnabled) {
      getLogger().info(marker, "[$prefix] ${message()}")
    }
  }

  /**
   * Logs at debug level
   * @param message provider
   */
  fun debug(message: () -> String) {
    if (getLogger().isDebugEnabled) {
      getLogger().debug(marker, "[$prefix] ${message()}")
    }
  }
}
