package lt.petuska.npm.publish.dsl

/**
 * Enum representation of NPM repository access
 */
enum class NpmAccess {
  PUBLIC,
  RESTRICTED;

  override fun toString(): String {
    return name.toLowerCase()
  }

  companion object {
    fun fromString(name: String): NpmAccess? = values().find { it.name.equals(name, true) }
  }
}
