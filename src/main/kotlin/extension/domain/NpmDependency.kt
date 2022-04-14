package dev.petuska.npm.publish.extension.domain

import dev.petuska.npm.publish.util.*
import org.gradle.api.provider.*
import org.gradle.api.tasks.*
import kotlin.math.*

interface NpmDependency : NamedInput {
  @get:Input
  val version: Property<String>

  @get:Input
  @get:Optional
  val scope: Property<Scope>

  companion object {
    val OPTIONAL = Scope.OPTIONAL
    val PEER = Scope.PEER
    val DEV = Scope.DEV
    val NORMAL = Scope.NORMAL
  }

  enum class Scope {
    OPTIONAL,
    PEER,
    DEV,
    NORMAL;

    inline val priority: Int get() = abs(0 - ordinal)
  }
}
