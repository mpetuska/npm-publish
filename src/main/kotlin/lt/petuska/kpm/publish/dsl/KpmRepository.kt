package lt.petuska.kpm.publish.dsl

import lt.petuska.kpm.publish.dsl.KpmPublishExtension.Companion.AUTH_TOKEN_PROP
import lt.petuska.kpm.publish.dsl.KpmPublishExtension.Companion.OTP_PROP
import lt.petuska.kpm.publish.util.fallbackDelegate
import lt.petuska.kpm.publish.util.gradleNullableProperty
import org.gradle.api.Project
import org.gradle.util.GUtil
import java.net.URI

class KpmRepository internal constructor(
  name: String,
  project: Project,
  kpmExtension: KpmPublishExtension
) {
  val name: String = GUtil.toLowerCamelCase(name)
  var access by kpmExtension.fallbackDelegate(KpmPublishExtension::access)
  var registry by project.gradleNullableProperty<URI>()
  var otp by project.gradleNullableProperty<String>(project.properties["$OTP_PROP.$name"] as String?)
  var authToken by project.gradleNullableProperty<String>(project.properties["$AUTH_TOKEN_PROP.$name"] as String?)

  internal fun validate(): KpmRepository? {
    return takeIf {
      registry != null && authToken != null
    }
  }
}
