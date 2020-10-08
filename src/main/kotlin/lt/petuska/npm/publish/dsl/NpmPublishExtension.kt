package lt.petuska.npm.publish.dsl

import groovy.lang.Closure
import lt.petuska.npm.publish.util.gradleNullableProperty
import lt.petuska.npm.publish.util.gradleProperty
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import java.io.File

open class NpmPublishExtension(private val project: Project) {
  var readme by project.gradleNullableProperty<File>()
  var organization by project.gradleNullableProperty<String>()
  var access by project.gradleProperty(NpmAccess.PUBLIC)

  val repositories: NpmRepositoryContainer = project.container(NpmRepository::class.java) { name ->
    NpmRepository(name, project, this)
  }

  fun repositories(config: NpmRepositoryContainer.() -> Unit) {
    repositories.configure(
      object : Closure<Unit>(this, this) {
        @Suppress("unused")
        fun doCall() {
          @Suppress("UNCHECKED_CAST")
          (delegate as? NpmRepositoryContainer)?.let {
            config(it)
          }
        }
      }
    )
  }

  fun repositories(config: Closure<Unit>) {
    repositories.configure(config)
  }

  fun NpmRepositoryContainer.repository(name: String, config: NpmRepository.() -> Unit): NpmRepository {
    val pub = NpmRepository(name, this@NpmPublishExtension.project, this@NpmPublishExtension).apply(config)
    add(pub)
    return pub
  }

  val publications: NpmPublicationContainer = project.container(NpmPublication::class.java) { name ->
    NpmPublication(name, project, this)
  }

  fun publications(config: NpmPublicationContainer.() -> Unit) {
    publications.configure(
      object : Closure<Unit>(this, this) {
        @Suppress("unused")
        fun doCall() {
          @Suppress("UNCHECKED_CAST")
          (delegate as? NpmPublicationContainer)?.let {
            config(it)
          }
        }
      }
    )
  }

  fun publications(config: Closure<Unit>) {
    publications.configure(config)
  }

  fun NpmPublicationContainer.publication(name: String, config: NpmPublication.() -> Unit): NpmPublication {
    val pub = NpmPublication(name, this@NpmPublishExtension.project, this@NpmPublishExtension).apply(config)
    add(pub)
    return pub
  }

  companion object {
    const val EXTENSION_NAME = "npmPublishing"
    const val AUTH_TOKEN_PROP = "npm.publish.authToken"
    const val OTP_PROP = "npm.publish.otp"
    const val DRY_RUN_PROP = "npm.publish.dry"
  }
}

internal typealias NpmRepositoryContainer = NamedDomainObjectContainer<NpmRepository>
internal typealias NpmPublicationContainer = NamedDomainObjectContainer<NpmPublication>
