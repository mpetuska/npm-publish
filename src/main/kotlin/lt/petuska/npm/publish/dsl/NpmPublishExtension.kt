package lt.petuska.npm.publish.dsl

import groovy.lang.Closure
import lt.petuska.npm.publish.util.gradleNullableProperty
import lt.petuska.npm.publish.util.gradleProperty
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import java.io.File

/**
 * Extension exposing npm-publish plugin configuration DSLs
 */
open class NpmPublishExtension(private val project: Project) {
  /**
   * A location of the default README file.
   * If set, the file will be used as a default readme for all publications that do not have one set explicitly.
   */
  var readme by project.gradleNullableProperty<File>()

  /**
   * Default [NpmPublication.scope]
   */
  var organization by project.gradleNullableProperty<String>()

  /**
   * Default [NpmRepository.access]
   *
   * Defaults to [NpmAccess.PUBLIC]
   */
  var access by project.gradleProperty(NpmAccess.PUBLIC)

  internal val repoConfigs = mutableListOf<Closure<Unit>>()
  internal val repositories: NpmRepositoryContainer = project.container(NpmRepository::class.java) { name ->
    NpmRepository(name, project, this)
  }

  internal fun repositories(index: Int, config: NpmRepositoryContainer.() -> Unit) {
    repoConfigs.add(
      index,
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

  /**
   * DSL exposing [NpmRepository] setup
   */
  fun repositories(config: NpmRepositoryContainer.() -> Unit) {
    repositories(repoConfigs.size, config)
  }

  /**
   * DSL exposing [NpmRepository] setup for groovy
   */
  fun repositories(config: Closure<Unit>) {
    repoConfigs.add(config)
  }

  /**
   * DSL exposing [NpmRepository] creation and configuration
   */
  fun NpmRepositoryContainer.repository(name: String, config: NpmRepository.() -> Unit): NpmRepository {
    val pub = NpmRepository(name, this@NpmPublishExtension.project, this@NpmPublishExtension).apply(config)
    add(pub)
    return pub
  }

  internal val pubConfigs = mutableListOf<Closure<Unit>>()
  internal val publications: NpmPublicationContainer = project.container(NpmPublication::class.java) { name ->
    NpmPublication(name, project, this)
  }

  internal fun publications(index: Int, config: NpmPublicationContainer.() -> Unit) {
    pubConfigs.add(
      index,
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

  /**
   * DSL exposing [NpmPublication] setup
   */
  fun publications(config: NpmPublicationContainer.() -> Unit) {
    publications(pubConfigs.size, config)
  }

  /**
   * DSL exposing [NpmPublication] setup for groovy
   */
  fun publications(config: Closure<Unit>) {
    pubConfigs.add(config)
  }

  /**
   * DSL exposing [NpmPublication] creation and configuration
   */
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
