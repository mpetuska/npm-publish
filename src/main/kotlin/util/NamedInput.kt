package dev.petuska.npm.publish.util

import org.gradle.api.*
import org.gradle.api.tasks.*

interface NamedInput : Named {
  @Input
  override fun getName(): String
}
