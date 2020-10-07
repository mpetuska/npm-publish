package lt.petuska.kpm.publish.dsl

import groovy.lang.Closure
import lt.petuska.kpm.publish.util.fallbackDelegate
import lt.petuska.kpm.publish.util.gradleNullableProperty
import lt.petuska.kpm.publish.util.gradleProperty
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.util.GUtil
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJsCompilation
import java.io.File

open class KpmPublishExtension(private val project: Project) {
  var readme by project.gradleNullableProperty<File>()
  var organization by project.gradleNullableProperty<String>()
  var registry by project.gradleProperty("https://registry.npmjs.org")
  var authToken by project.gradleNullableProperty(project.properties[AUTH_TOKEN_PROP] as String?)
  var otp by project.gradleNullableProperty(project.properties[OTP_PROP] as String?)
  var access by project.gradleProperty("public")

  var publications: KpmPublicationContainer = project.container(KpmPublication::class.java) { name ->
    KpmPublication(name, project, this)
  }

  fun publications(config: KpmPublicationContainer.() -> Unit) {
    publications.configure(
      object : Closure<Unit>(this, this) {
        @Suppress("unused")
        fun doCall() {
          @Suppress("UNCHECKED_CAST")
          (delegate as? KpmPublicationContainer)?.let {
            config(it)
          }
        }
      }
    )
  }

  fun publications(config: Closure<Unit>) {
    publications.configure(config)
  }

  fun KpmPublicationContainer.publication(name: String, config: KpmPublication.() -> Unit): KpmPublication {
    val pub = KpmPublication(name, this@KpmPublishExtension.project, this@KpmPublishExtension).apply(config)
    publications.add(pub)
    return pub
  }

  companion object {
    const val EXTENSION_NAME = "kpmPublish"
    const val AUTH_TOKEN_PROP = "kpm.publish.authToken"
    const val OTP_PROP = "kpm.publish.otp"
    const val DRY_RUN_PROP = "kpm.publish.dry"
  }
}

internal typealias KpmPublicationContainer = NamedDomainObjectContainer<KpmPublication>

open class KpmPublication(
  name: String,
  project: Project,
  extension: KpmPublishExtension
) {
  val name: String = GUtil.toLowerCamelCase(name)
  var compilation by project.gradleNullableProperty<KotlinJsCompilation>()
  var moduleName: String by project.gradleProperty(project.name)
  var scope by extension.fallbackDelegate(KpmPublishExtension::organization)
  var readme by extension.fallbackDelegate(KpmPublishExtension::readme)
  var registry by extension.fallbackDelegate(KpmPublishExtension::registry)
  var authToken by extension.fallbackDelegate(KpmPublishExtension::authToken)
  var destinationDir by project.gradleProperty(File("${project.buildDir}/publications/kpm/$name"))
  var otp by extension.fallbackDelegate(KpmPublishExtension::otp)
  var access by extension.fallbackDelegate(KpmPublishExtension::access)
}
