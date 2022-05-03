package dev.petuska.npm.publish.extension

import dev.petuska.npm.publish.NpmPublishPlugin
import dev.petuska.npm.publish.extension.domain.NpmAccess
import dev.petuska.npm.publish.extension.domain.NpmAccessScope
import dev.petuska.npm.publish.extension.domain.NpmPackage
import dev.petuska.npm.publish.extension.domain.NpmPackages
import dev.petuska.npm.publish.extension.domain.NpmRegistries
import dev.petuska.npm.publish.extension.domain.NpmRegistry
import dev.petuska.npm.publish.task.NodeExecTask
import dev.petuska.npm.publish.task.NpmPackTask
import dev.petuska.npm.publish.task.NpmPublishTask
import dev.petuska.npm.publish.util.WithGradleFactories
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.Property
import java.net.URI

/**
 * An extension for npm-publish plugin configuration
 * @see [NpmPublishPlugin]
 */
@Suppress("unused", "LeakingThis")
public abstract class NpmPublishExtension : WithGradleFactories(), ExtensionAware, NpmAccessScope {
  public companion object {
    internal const val NAME = "npmPublish"
  }

  /**
   * Base NodeJS directory to be used when executing npm commands.
   * Defaults to `NODE_HOME` env variable.
   *
   * @see [NodeExecTask.nodeHome]
   */
  public abstract val nodeHome: DirectoryProperty

  /**
   * A location of the default `README.md` file.
   * If set, it will be used as a default for all packages that do not have one set explicitly.
   * @see [NpmPackage.readme]
   */
  public abstract val readme: RegularFileProperty

  /**
   * A location of the default `.npmignore` file.
   * If set, it will be used as a default for all packages that do not have one set explicitly.
   * @see [NpmPackage.npmIgnore]
   */
  public abstract val npmIgnore: RegularFileProperty

  /**
   * Default package scope.
   * If set, it will be used as a default for all packages that do not have one set explicitly.
   * @see [NpmPackage.scope]
   */
  public abstract val organization: Property<String>

  /**
   * Default package version. Defaults to [Project.getVersion] or `rootProject.version`.
   * If set, it will be used as a default for all packages that do not have one set explicitly.
   * @see [NpmPackage.version]
   */
  public abstract val version: Property<String>

  /**
   * Default package access when publishing to npm registries.
   * Defaults to [NpmAccess.PUBLIC]
   * @see [NpmRegistry.access]
   */
  public abstract val access: Property<NpmAccess>

  /**
   * Specifies if a dry-run should be added to the npm command arguments by default. Dry run does all the
   * normal run des except actual file uploading. Defaults to `false`.
   * @see [NpmRegistry.dry]
   * @see [NpmPackTask.dry]
   * @see [NpmPublishTask.dry]
   */
  public abstract val dry: Property<Boolean>

  /**
   * A container for npm package configurations
   * @see [NpmPackage]
   */
  public abstract val packages: NpmPackages

  /**
   * A container for npm registry configurations
   * @see [NpmRegistry]
   */
  public abstract val registries: NpmRegistries

  // region DSL

  /**
   * Convenience DSL to configure npm packages
   * @param action to apply
   * @see [NpmPackage]
   */
  public fun packages(action: Action<NpmPackages>) {
    action.execute(packages)
  }

  /**
   * Convenience DSL to configure npm registries
   * @param action to apply
   * @see [NpmRegistry]
   */
  public fun registries(action: Action<NpmRegistries>) {
    action.execute(registries)
  }

  /**
   * Registers [npmjs.com](https://npmjs.com) registry.
   * [More info](https://docs.npmjs.com/creating-and-publishing-unscoped-public-packages)
   * @param action to apply
   * @see [NpmRegistry]
   */
  public fun NpmRegistries.npmjs(action: Action<NpmRegistry>): NamedDomainObjectProvider<NpmRegistry> =
    register("npmjs") {
      it.uri.set(URI("https://registry.npmjs.org"))
      action.execute(it)
    }

  /**
   * Registers GitHub Packages [npm.pkg.github.com](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-npm-registry) registry.
   * [More info](https://docs.npmjs.com/creating-and-publishing-unscoped-public-packages)
   * @param action to apply
   * @see [NpmRegistry]
   */
  public fun NpmRegistries.gitHub(action: Action<NpmRegistry>): NamedDomainObjectProvider<NpmRegistry> =
    register("gitHub") {
      it.uri.set(URI("https://npm.pkg.github.com/"))
      action.execute(it)
    }

  // endregion
}
