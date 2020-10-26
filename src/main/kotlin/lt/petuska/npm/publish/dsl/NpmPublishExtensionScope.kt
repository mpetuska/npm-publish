package lt.petuska.npm.publish.dsl

interface NpmPublishExtensionScope {
  val PUBLIC: NpmAccess get() = NpmAccess.PUBLIC
  val RESTRICTED: NpmAccess get() = NpmAccess.RESTRICTED
}
