package dev.petuska.npm.publish.util

import org.gradle.api.tasks.Internal
import org.slf4j.Logger
import org.slf4j.Marker
import org.slf4j.MarkerFactory

interface PluginLogger {
  companion object {
    internal const val prefix = "[npm-publish] "
    internal val marker: Marker = MarkerFactory.getMarker("npm-publish")
  }

  @Internal
  fun getLogger(): Logger

  fun error(message: () -> String) {
    if (getLogger().isErrorEnabled) {
      getLogger().error(marker, prefix + message())
    }
  }

  fun warn(message: () -> String) {
    if (getLogger().isWarnEnabled) {
      getLogger().warn(marker, prefix + message())
    }
  }

  fun info(message: () -> String) {
    if (getLogger().isInfoEnabled) {
      getLogger().info(marker, prefix + message())
    }
  }

  fun debug(message: () -> String) {
    if (getLogger().isDebugEnabled) {
      getLogger().debug(marker, prefix + message())
    }
  }
}
