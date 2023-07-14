package dev.petuska.npm.publish.util

import org.gradle.api.tasks.Internal
import org.slf4j.Logger
import org.slf4j.Marker
import org.slf4j.MarkerFactory

/**
 * Standardised logging utilities with the plugin name prefix
 */
public interface PluginLogger {
  public companion object {
    internal const val prefix = "[npm-publish] "
    internal val marker: Marker = MarkerFactory.getMarker("npm-publish")
    internal fun wrap(logger: Logger): PluginLogger = object : PluginLogger {
      override fun getLogger(): Logger = logger
    }
  }

  /**
   * Logger provider
   * @return logger to use for all logging events
   */
  @Internal
  public fun getLogger(): Logger

  /**
   * Logs at error level
   * @param message provider
   */
  public fun error(message: () -> String) {
    if (getLogger().isErrorEnabled) {
      getLogger().error(marker, prefix + message())
    }
  }

  /**
   * Logs at warn level
   * @param message provider
   */
  public fun warn(message: () -> String) {
    if (getLogger().isWarnEnabled) {
      getLogger().warn(marker, prefix + message())
    }
  }

  /**
   * Logs at info level
   * @param message provider
   */
  public fun info(message: () -> String) {
    if (getLogger().isInfoEnabled) {
      getLogger().info(marker, prefix + message())
    }
  }

  /**
   * Logs at debug level
   * @param message provider
   */
  public fun debug(message: () -> String) {
    if (getLogger().isDebugEnabled) {
      getLogger().debug(marker, prefix + message())
    }
  }
}
