package lt.petuska.npm.publish.dsl

import lt.petuska.npm.publish.dsl.NpmPublishExtension.Companion.AUTH_TOKEN_PROP
import lt.petuska.npm.publish.dsl.NpmPublishExtension.Companion.OTP_PROP
import lt.petuska.npm.publish.util.fallbackDelegate
import lt.petuska.npm.publish.util.gradleNullableProperty
import org.gradle.api.Project
import org.gradle.util.GUtil
import java.net.URI

class NpmRepository internal constructor(
  name: String,
  project: Project,
  npmExtension: NpmPublishExtension
) {
  val name: String = GUtil.toLowerCamelCase(name)
  var access by npmExtension.fallbackDelegate(NpmPublishExtension::access)
  var registry by project.gradleNullableProperty<URI>()
  var otp by project.gradleNullableProperty<String>(project.properties["$OTP_PROP.$name"] as String?)
  var authToken by project.gradleNullableProperty<String>(project.properties["$AUTH_TOKEN_PROP.$name"] as String?)

  internal fun validate(): NpmRepository? {
    return takeIf {
      registry != null && authToken != null
    }
  }
}
