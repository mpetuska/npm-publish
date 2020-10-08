package lt.petuska.npm.publish.dsl

enum class NpmAccess {
  PUBLIC,
  RESTRICTED;

  override fun toString(): String {
    return name.toLowerCase()
  }
}
