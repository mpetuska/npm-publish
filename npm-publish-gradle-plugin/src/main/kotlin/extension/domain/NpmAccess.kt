package dev.petuska.npm.publish.extension.domain

import java.util.Locale

/** Enum representation of NPM repository access */
public enum class NpmAccess {
  PUBLIC,
  RESTRICTED;

  override fun toString(): String {
    return name.lowercase(Locale.getDefault())
  }

  public companion object {
    /**
     * Converts a given string to an instance of [NpmAccess] by name ignoring case
     * @param name of the [NpmAccess] instance
     * @return an instance of [NpmAccess]
     */
    public fun fromString(name: String): NpmAccess = values().first { it.name.equals(name, true) }
  }
}
