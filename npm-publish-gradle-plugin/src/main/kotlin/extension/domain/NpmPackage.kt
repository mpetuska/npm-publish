package dev.petuska.npm.publish.extension.domain

import dev.petuska.npm.publish.extension.NpmPublishExtension
import dev.petuska.npm.publish.extension.domain.json.PackageJson
import dev.petuska.npm.publish.util.NamedInput
import dev.petuska.npm.publish.util.WithGradleFactories
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity

/**
 * The main configuration for a package
 */
@Suppress("unused", "LeakingThis", "MemberVisibilityCanBePrivate")
public abstract class NpmPackage : NamedInput, WithGradleFactories() {

  /**
   * Optional npm scope. If set, package name will be constructed as `@{scope}/{moduleName}`.
   * Defaults to [NpmPublishExtension.organization].
   * @see [NpmPublishExtension.organization]
   */
  @get:Input
  @get:Optional
  public abstract val scope: Property<String>

  /**
   * NPM package name.
   * Defaults to [Project.getName].
   * @see [Project.getName]
   */
  @get:Input
  public abstract val packageName: Property<String>

  /**
   * NPM package version.
   * Defaults to [NpmPublishExtension.version].
   * @see [NpmPublishExtension.version]
   */
  @get:Input
  public abstract val version: Property<String>

  /**
   * Main js entry file.
   * Can also be set via [packageJsonFile], [packageJsonTemplateFile] or [packageJson]
   * @see [PackageJson.main]
   */
  @get:Input
  @get:Optional
  public abstract val main: Property<String>

  /**
   * Main d.ts entry file.
   * Can also be set via [packageJsonFile], [packageJsonTemplateFile] or [packageJson]
   * @see [PackageJson.types]
   */
  @get:Input
  @get:Optional
  public abstract val types: Property<String>

  /**
   * A location of the `README.md` file.
   * If set, the file will be moved to package assembly root and renamed to README.MD (regardless of the actual name).
   * Defaults to [NpmPublishExtension.readme]
   * @see [NpmPublishExtension.readme]
   */
  @get:InputFile
  @get:Optional
  @get:PathSensitive(PathSensitivity.RELATIVE)
  public abstract val readme: RegularFileProperty

  /**
   * A location of the `.npmignore` file.
   * Defaults to [NpmPublishExtension.npmIgnore]
   * @see [NpmPublishExtension.npmIgnore]
   */
  @get:InputFile
  @get:Optional
  @get:PathSensitive(PathSensitivity.RELATIVE)
  public abstract val npmIgnore: RegularFileProperty

  /**
   * Files that should be assembled for this package
   */
  @get:InputFiles
  @get:PathSensitive(PathSensitivity.RELATIVE)
  public abstract val files: ConfigurableFileCollection

  /**
   * `package.json` customisation container.
   * @see [packageJsonFile]
   * @see [packageJsonTemplateFile]
   */
  @get:Nested
  @get:Optional
  public abstract val packageJson: Property<PackageJson>

  /**
   * If set, fully disregards [main], [types] & [packageJson] configurations. Used as-is.
   * @see [packageJson]
   * @see [packageJsonTemplateFile]
   */
  @get:InputFile
  @get:Optional
  @get:PathSensitive(PathSensitivity.RELATIVE)
  public abstract val packageJsonFile: RegularFileProperty

  /**
   * Similar to [packageJsonFile] except allows the options to be overridden by the [packageJson] options.
   * @see [packageJson]
   * @see [packageJsonFile]
   */
  @get:InputFile
  @get:Optional
  @get:PathSensitive(PathSensitivity.RELATIVE)
  public abstract val packageJsonTemplateFile: RegularFileProperty

  /**
   * Package's npm dependencies.
   */
  @get:Nested
  public abstract val dependencies: NpmDependencies

  //region DSL
  /**
   * Convenience DSL to configure package's files
   * @param action to apply
   */
  public fun files(action: Action<ConfigurableFileCollection>) {
    action.execute(files)
  }

  /**
   * Convenience DSL to customise `package.json`
   * @param action to apply
   */
  public fun packageJson(action: Action<PackageJson>) {
    packageJson.configure(config = action)
  }

  /**
   * Convenience DSL to configure package's dependencies
   * @param action to apply
   */
  public fun dependencies(action: Action<NpmDependencies>) {
    action.execute(dependencies)
  }

  /**
   * Registers an arbitrary npm dependency for the package
   * @param name of the dependency
   * @param version of the dependency
   * @param type of the dependency
   * @param action to apply
   * @return registered dependency
   */
  public fun NpmDependencies.dependency(
    name: String,
    version: String,
    type: NpmDependency.Type,
    action: Action<NpmDependency> = Action { }
  ): NamedDomainObjectProvider<NpmDependency> = register(name) {
    it.type.set(type)
    it.version.set(version)
    action.execute(it)
  }

  /**
   * Registers a normal npm dependency for the package
   * @param name of the dependency
   * @param version of the dependency
   * @param action to apply
   * @return registered dependency
   */
  public fun NpmDependencies.normal(
    name: String,
    version: String,
    action: Action<NpmDependency> = Action { }
  ): NamedDomainObjectProvider<NpmDependency> = dependency(name, version, NpmDependency.Type.NORMAL, action)

  /**
   * Registers an optional npm dependency for the package
   * @param name of the dependency
   * @param version of the dependency
   * @param action to apply
   * @return registered dependency
   */
  public fun NpmDependencies.optional(
    name: String,
    version: String,
    action: Action<NpmDependency> = Action { }
  ): NamedDomainObjectProvider<NpmDependency> = dependency(name, version, NpmDependency.Type.OPTIONAL, action)

  /**
   * Registers a dev npm dependency for the package
   * @param name of the dependency
   * @param version of the dependency
   * @param action to apply
   * @return registered dependency
   */
  public fun NpmDependencies.dev(
    name: String,
    version: String,
    action: Action<NpmDependency> = Action { }
  ): NamedDomainObjectProvider<NpmDependency> = dependency(name, version, NpmDependency.Type.DEV, action)

  /**
   * Registers a peer npm dependency for the package
   * @param name of the dependency
   * @param version of the dependency
   * @param action to apply
   * @return registered dependency
   */
  public fun NpmDependencies.peer(
    name: String,
    version: String,
    action: Action<NpmDependency> = Action { }
  ): NamedDomainObjectProvider<NpmDependency> = dependency(name, version, NpmDependency.Type.PEER, action)

  // endregion
}

public typealias NpmPackages = NamedDomainObjectContainer<NpmPackage>
