package dev.petuska.npm.publish.dsl

import dev.petuska.npm.publish.delegate.fallbackDelegate
import dev.petuska.npm.publish.delegate.or
import dev.petuska.npm.publish.delegate.propertyDelegate
import dev.petuska.npm.publish.npmPublishing
import dev.petuska.npm.publish.util.notFalse
import org.gradle.api.Project
import java.net.URI

/**
 * Npm repository (registry) configuration container
 */
class NpmRepository internal constructor(
  /**
   * Repository name.
   */
  val name: String,
  private val project: Project,
  npmExtension: NpmPublishExtension
) {
  private val propGroup = "$PROP_PREFIX.$name"

  /**
   * Repository access.
   */
  var access: NpmAccess by project.propertyDelegate(propGroup) { NpmAccess.fromString(it) } or npmExtension.fallbackDelegate(NpmPublishExtension::access)

  /**
   * NPM Registry uri to publish packages to. Should include schema domain and path if required
   */
  var registry: URI? by project.propertyDelegate(propGroup) { URI(it) }

  /**
   * Optional OTP to use when authenticating with the registry.
   */
  var otp: String? by project.propertyDelegate(propGroup) { it }

  /**
   * Auth token to use when authenticating with the registry
   */
  var authToken: String? by project.propertyDelegate(propGroup) { it }

  /**
   * Overrides [NpmPublishExtension.dry] option for this repository
   */
  var dry: Boolean by project.propertyDelegate(propGroup) { it.notFalse() } or npmExtension.fallbackDelegate(NpmPublishExtension::dry)

  internal fun validate(): NpmRepository? {
    return takeIf {
      registry != null && (authToken != null || project.npmPublishing.dry)
    }
  }

  companion object {
    const val PROP_PREFIX = "repository"
  }
}
