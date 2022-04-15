package dev.petuska.npm.publish.extension

import dev.petuska.npm.publish.extension.domain.NpmAccess
import dev.petuska.npm.publish.extension.domain.NpmAccessScope
import dev.petuska.npm.publish.extension.domain.NpmPackages
import dev.petuska.npm.publish.extension.domain.NpmRegistries
import dev.petuska.npm.publish.util.WithGradleFactories
import org.gradle.api.*
import org.gradle.api.file.*
import org.gradle.api.model.*
import org.gradle.api.plugins.*
import org.gradle.api.provider.*
import javax.inject.*

@Suppress("unused", "LeakingThis")
abstract class NpmPublishExtension : WithGradleFactories(), ExtensionAware, NpmAccessScope {
  companion object {
    internal const val NAME = "npmPublish"
    val PUBLIC = NpmAccess.PUBLIC
    val RESTRICTED = NpmAccess.RESTRICTED
  }

  /**
   * Base NodeJS directory to be used when building and publishing the publications.
   * Defaults to 'NODE_HOME' env variable.
   */
  abstract val nodeHome: DirectoryProperty

  /**
   * A location of the default README file. If set, the file will be used as a default readme for
   * all publications that do not have one set explicitly.
   */
  abstract val readme: RegularFileProperty

  /**
   * Default [NpmPublication.scope]
   */
  abstract val organization: Property<String>

  /**
   * NPM package version. Defaults to [Project.getVersion] or rootProject.version.
   */
  abstract val version: Property<String>

  /**
   * Default [NpmRepository.access]
   *
   * Defaults to [NpmAccess.PUBLIC]
   */
  abstract val access: Property<NpmAccess>

  /**
   * Specifies if a dry-run should be added to the npm command arguments. Dry run does all the
   * normal run des except actual file uploading. Defaults to `npm.publish.dry` project property if
   * set or `false` otherwise.
   */
  abstract val dry: Property<Boolean>

  abstract val packages: NpmPackages

  abstract val registries: NpmRegistries

  // region DSL

  fun packages(action: Action<NpmPackages>) {
    action.execute(packages)
  }

  fun registries(action: Action<NpmRegistries>) {
    action.execute(registries)
  }

  // endregion
}
