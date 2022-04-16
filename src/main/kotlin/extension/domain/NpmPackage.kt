package dev.petuska.npm.publish.extension.domain

import dev.petuska.npm.publish.extension.domain.json.PackageJson
import dev.petuska.npm.publish.util.*
import org.gradle.api.*
import org.gradle.api.file.*
import org.gradle.api.provider.*
import org.gradle.api.tasks.*

@Suppress("unused", "LeakingThis", "MemberVisibilityCanBePrivate")
abstract class NpmPackage : NamedInput, WithGradleFactories() {

  /**
   * Optional npm scope. If set, package name will be constructed as `@{scope}/{moduleName}`.
   * Defaults to [NpmPublishExtension.organization].
   */
  @get:Input
  @get:Optional
  abstract val scope: Property<String>

  /**
   * NPM package name.
   * Defaults to [Project.getName].
   */
  @get:Input
  abstract val packageName: Property<String>

  /**
   * NPM package version.
   * Defaults to [NpmPublishExtension.version].
   */
  @get:Input
  abstract val version: Property<String>

  /**
   * Main js entry file. Can also be set via [packageJson] DSL.
   * @see [PackageJson.main]
   */
  @get:Input
  abstract val main: Property<String>

  /**
   * Main d.ts entry file. Can also be set via [packageJson] DSL.
   * @see [PackageJson.types]
   */
  @get:Input
  @get:Optional
  abstract val types: Property<String>

  /**
   * A location of the main README file. If set, the file will be moved to package assembly root and
   * renamed to README.MD (regardless of the actual name). Defaults to [NpmPublishExtension.readme]
   */
  @get:InputFile
  @get:Optional
  abstract val readme: RegularFileProperty

  @get:InputFile
  @get:Optional
  abstract val npmIgnore: RegularFileProperty

  /**
   * Files that compose this publication
   */
  @get:InputFiles
  abstract val files: ConfigurableFileCollection

  @get:Nested
  @get:Optional
  abstract val packageJson: Property<PackageJson>

  /**
   * If set, fully disregards [packageJson] DSL configuration and used the specified raw
   * package.json file as-is.
   */
  @get:InputFile
  @get:Optional
  abstract val packageJsonFile: RegularFileProperty

  /**
   * Similar to [packageJsonFile] except allows the options to be overridden by the [packageJson]
   * DSL.
   */
  @get:InputFile
  @get:Optional
  abstract val packageJsonTemplateFile: RegularFileProperty

  /**
   * Package's npm dependencies
   */
  @get:Nested
  abstract val dependencies: NpmDependencies

  //region DSL
  fun files(action: Action<ConfigurableFileCollection>) {
    action.execute(files)
  }

  fun packageJson(action: Action<PackageJson>) {
    packageJson.configure(config = action)
  }

  fun dependencies(action: Action<NpmDependencies>) {
    action.execute(dependencies)
  }

  fun NpmDependencies.dependency(
    name: String,
    version: String,
    scope: NpmDependency.Scope,
    action: Action<NpmDependency> = Action { }
  ): NamedDomainObjectProvider<NpmDependency> = register(name) {
    it.scope.set(scope)
    it.version.set(version)
    action.execute(it)
  }

  fun NpmDependencies.normal(
    name: String,
    version: String,
    action: Action<NpmDependency> = Action { }
  ): NamedDomainObjectProvider<NpmDependency> = dependency(name, version, NpmDependency.Scope.NORMAL, action)

  fun NpmDependencies.optional(
    name: String,
    version: String,
    action: Action<NpmDependency> = Action { }
  ): NamedDomainObjectProvider<NpmDependency> = dependency(name, version, NpmDependency.Scope.OPTIONAL, action)

  fun NpmDependencies.dev(
    name: String,
    version: String,
    action: Action<NpmDependency> = Action { }
  ): NamedDomainObjectProvider<NpmDependency> = dependency(name, version, NpmDependency.Scope.DEV, action)

  fun NpmDependencies.peer(
    name: String,
    version: String,
    action: Action<NpmDependency> = Action { }
  ): NamedDomainObjectProvider<NpmDependency> = dependency(name, version, NpmDependency.Scope.PEER, action)

  // endregion
}

typealias NpmPackages = NamedDomainObjectContainer<NpmPackage>
