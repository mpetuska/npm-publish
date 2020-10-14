package lt.petuska.npm.publish.dsl

import lt.petuska.npm.publish.dsl.NpmPublishExtension.Companion.AUTH_TOKEN_PROP
import lt.petuska.npm.publish.dsl.NpmPublishExtension.Companion.OTP_PROP
import lt.petuska.npm.publish.util.fallbackDelegate
import lt.petuska.npm.publish.util.gradleNullableProperty
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.util.GUtil
import java.net.URI

/**
 * Npm repository (registry) configuration container
 */
class NpmRepository internal constructor(
  name: String,
  project: Project,
  npmExtension: NpmPublishExtension
) {
  /**
   * Repository name. Always in lowerCamelCase.
   */
  val name: String = GUtil.toLowerCamelCase(name)

  /**
   * Repository access.
   */
  @get:Input
  var access by npmExtension.fallbackDelegate(NpmPublishExtension::access)

  /**
   * NPM Registry uri to publish packages to. Should include schema domain and path if required
   */
  @get:Input
  var registry by project.gradleNullableProperty<URI>()

  /**
   * Optional OTP to use when authenticating with the registry.
   */
  @get:Input
  @get:Optional
  var otp by project.gradleNullableProperty<String>(project.properties["$OTP_PROP.$name"] as String?)

  /**
   * Auth token to use when authenticating with the registry
   */
  @get:Input
  @get:Optional
  var authToken by project.gradleNullableProperty<String>(project.properties["$AUTH_TOKEN_PROP.$name"] as String?)

  internal fun validate(): NpmRepository? {
    return takeIf {
      registry != null && authToken != null
    }
  }
}
