package dev.petuska.npm.publish.dsl

import dev.petuska.npm.publish.delegate.fallbackDelegate
import dev.petuska.npm.publish.delegate.or
import dev.petuska.npm.publish.delegate.propertyDelegate
import dev.petuska.npm.publish.util.notFalse
import groovy.lang.Closure
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import java.io.File

/**
 * Extension exposing npm-publish plugin configuration DSLs
 */
open class NpmPublishExtension(private val project: Project) : NpmPublishExtensionScope {
  /**
   * A location of the default README file.
   * If set, the file will be used as a default readme for all publications that do not have one set explicitly.
   */
  var readme: File? by project.propertyDelegate { File(it) }

  /**
   * Default [NpmPublication.scope]
   */
  var organization: String? by project.propertyDelegate { it }

  /**
   * NPM package version.
   * Defaults to [Project.getVersion] or rootProject.version.
   */
  var version: String? by project.propertyDelegate { it } or project.fallbackDelegate { version.toString() }

  /**
   * Default [NpmRepository.access]
   *
   * Defaults to [NpmAccess.PUBLIC]
   */
  var access: NpmAccess by project.propertyDelegate(default = NpmAccess.PUBLIC) { NpmAccess.fromString(it) }

  /**
   * Sets global default flag
   * @see [NpmPublication.bundleKotlinDependencies]
   */
  var bundleKotlinDependencies: Boolean by project.propertyDelegate(default = true) { it.notFalse() }

  /**
   * Sets global default flag
   * @see [NpmPublication.shrinkwrapBundledDependencies]
   */
  var shrinkwrapBundledDependencies: Boolean by project.propertyDelegate(default = true) { it.notFalse() }

  /**
   * Specifies if a dry-run should be added to the npm command arguments. Dry run does all the normal run des except actual file uploading.
   * Defaults to `npm.publish.dry` project property if set or `false` otherwise.
   */
  var dry: Boolean by project.propertyDelegate(default = false) { it.notFalse() }

  internal val repoConfigs = mutableListOf<Closure<Unit>>()
  internal val repositories: NpmRepositoryContainer = project.container(NpmRepository::class.java) { name ->
    NpmRepository(name, project, this)
  }

  private fun repositories(index: Int, config: NpmRepositoryContainer.() -> Unit) {
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
   * DSL exposing [NpmPublication] creation and configuration.
   * Will look for existing publication with the same name or create a new one before applying the configuration
   */
  fun NpmPublicationContainer.publication(name: String, config: NpmPublication.() -> Unit): NpmPublication {
    val pub = findByName(name) ?: NpmPublication(name, this@NpmPublishExtension.project, this@NpmPublishExtension).also {
      add(it)
    }
    pub.apply(config)
    return pub
  }

  companion object {
    internal const val EXTENSION_NAME = "npmPublishing"
  }
}

internal typealias NpmRepositoryContainer = NamedDomainObjectContainer<NpmRepository>
internal typealias NpmPublicationContainer = NamedDomainObjectContainer<NpmPublication>
