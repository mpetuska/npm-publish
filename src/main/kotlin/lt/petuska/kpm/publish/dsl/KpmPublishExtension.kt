package lt.petuska.kpm.publish.dsl

import groovy.lang.Closure
import lt.petuska.kpm.publish.util.gradleNullableProperty
import lt.petuska.kpm.publish.util.gradleProperty
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import java.io.File

open class KpmPublishExtension(private val project: Project) {
  var readme by project.gradleNullableProperty<File>()
  var organization by project.gradleNullableProperty<String>()
  var access by project.gradleProperty(NpmAccess.PUBLIC)

  val repositories: KpmRepositoryContainer = project.container(KpmRepository::class.java) { name ->
    KpmRepository(name, project, this)
  }

  fun repositories(config: KpmRepositoryContainer.() -> Unit) {
    repositories.configure(
      object : Closure<Unit>(this, this) {
        @Suppress("unused")
        fun doCall() {
          @Suppress("UNCHECKED_CAST")
          (delegate as? KpmRepositoryContainer)?.let {
            config(it)
          }
        }
      }
    )
  }

  fun repositories(config: Closure<Unit>) {
    repositories.configure(config)
  }

  fun KpmRepositoryContainer.repository(name: String, config: KpmRepository.() -> Unit): KpmRepository {
    val pub = KpmRepository(name, this@KpmPublishExtension.project, this@KpmPublishExtension).apply(config)
    add(pub)
    return pub
  }

  val publications: KpmPublicationContainer = project.container(KpmPublication::class.java) { name ->
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
    add(pub)
    return pub
  }

  companion object {
    const val EXTENSION_NAME = "kpmPublish"
    const val AUTH_TOKEN_PROP = "kpm.publish.authToken"
    const val OTP_PROP = "kpm.publish.otp"
    const val DRY_RUN_PROP = "kpm.publish.dry"
  }
}

internal typealias KpmRepositoryContainer = NamedDomainObjectContainer<KpmRepository>
internal typealias KpmPublicationContainer = NamedDomainObjectContainer<KpmPublication>
