package dev.petuska.npm.publish.extension.domain

import java.util.*

/** Enum representation of NPM repository access */
enum class NpmAccess {
  PUBLIC,
  RESTRICTED;

  override fun toString(): String {
    return name.lowercase(Locale.getDefault())
  }

  companion object {
    fun fromString(name: String): NpmAccess = values().first { it.name.equals(name, true) }
  }
}
