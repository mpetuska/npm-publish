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
}
