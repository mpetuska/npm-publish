package dev.petuska.npm.publish.extension.domain

import dev.petuska.npm.publish.util.*
import org.gradle.api.*
import org.gradle.api.provider.*
import org.gradle.api.tasks.*
import java.net.*

@Suppress("unused", "LeakingThis")
abstract class NpmRegistry : NamedInput {
  /** Repository access. */
  @get:Input
  abstract val access: Property<NpmAccess>

  /** NPM Registry uri to publish packages to. Should include schema domain and path if required */
  @get:Input
  abstract val uri: Property<URI>

  /** Optional OTP to use when authenticating with the registry. */
  @get:Input
  @get:Optional
  abstract val otp: Property<String>

  /** Auth token to use when authenticating with the registry */
  @get:Input
  @get:Optional
  abstract val authToken: Property<String>

  fun Property<URI>.set(uri: String) {
    set(URI(uri))
  }

  init {
    access.convention(NpmAccess.PUBLIC)
  }
}

typealias NpmRegistries = NamedDomainObjectContainer<NpmRegistry>
