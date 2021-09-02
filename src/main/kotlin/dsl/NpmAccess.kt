package dev.petuska.npm.publish.dsl

import java.util.Locale

/**
 * Enum representation of NPM repository access
 */
enum class NpmAccess {
  PUBLIC,
  RESTRICTED;

  override fun toString(): String {
    return name.lowercase(Locale.getDefault())
  }

  companion object {
    fun fromString(name: String): NpmAccess? = values().find { it.name.equals(name, true) }
  }
}
