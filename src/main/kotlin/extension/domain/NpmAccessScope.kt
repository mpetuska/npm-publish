package dev.petuska.npm.publish.extension.domain

@Suppress("PropertyName", "unused")
interface NpmAccessScope {
  val PUBLIC: NpmAccess
    get() = NpmAccess.PUBLIC
  val RESTRICTED: NpmAccess
    get() = NpmAccess.RESTRICTED
}
