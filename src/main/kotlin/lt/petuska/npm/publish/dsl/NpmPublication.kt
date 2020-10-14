package lt.petuska.npm.publish.dsl

import lt.petuska.npm.publish.util.fallbackDelegate
import lt.petuska.npm.publish.util.gradleNullableProperty
import lt.petuska.npm.publish.util.gradleProperty
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.util.GUtil
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJsCompilation
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmDependency
import java.io.File

/**
 * NPM publication (package) configuration container
 */
class NpmPublication internal constructor(
  name: String,
  private val project: Project,
  extension: NpmPublishExtension,
  /**
   * A container for package's npm dependencies
   */
  @get:Internal
  val npmDependencies: MutableList<NpmDependency> = mutableListOf()
) {
  @get:Input
  internal val npmDependenciesStr: List<String>
    get() = npmDependencies.map { "$it" }

  /**
   * Publication name. Always in lowerCamelCase.
   */
  @get:Input
  val name: String = GUtil.toLowerCamelCase(name)

  /**
   * NPM module name.
   * Defaults to [Project.getName].
   */
  @get:Input
  var moduleName by project.gradleProperty(project.name)

  /**
   * NPM package version.
   * Defaults to [Project.getVersion].
   */
  @get:Input
  var version by project.gradleProperty(project.version as String)

  /**
   * Optional npm scope. If set, package name will be constructed as `@{scope}/{moduleName}`.
   * Defaults to [NpmPublishExtension.organization].
   */
  @get:Input
  @get:Optional
  var scope by extension.fallbackDelegate(NpmPublishExtension::organization)

  /**
   * A location of the main README file.
   * If set, the file will be moved to package assembly root and renamed to README.MD (regardless of the actual name).
   * Defaults to [NpmPublishExtension.readme]
   */
  @get:InputFile
  @get:Optional
  var readme by extension.fallbackDelegate(NpmPublishExtension::readme)

  /**
   * Publication assembly directory.
   * Defaults to `${project.buildDir}/publications/npm/${this.name}`
   */
  @get:OutputDirectory
  var destinationDir by project.gradleProperty(File("${project.buildDir}/publications/npm/${this.name}"))

  /**
   * Main js entry file. Can also be set via [packageJson] DSL.
   */
  @get:Input
  @get:Optional
  var main by project.gradleNullableProperty<String>()

  /**
   * Base NodeJS directory to be used when building and publishing the publication. Defaults to 'NODE_HOME' env variable.
   */
  @get:InputDirectory
  @get:Optional
  var nodeJsDir by project.gradleNullableProperty(System.getenv("NODE_HOME")?.let(::File))

  @get:Internal
  internal var compilation by project.gradleNullableProperty<KotlinJsCompilation>()
  @get:Internal
  internal var fileSpecs = mutableListOf<CopySpec.(File) -> Unit>()
  @get:Internal
  internal var packageJsonSpecs = mutableListOf<PackageJson.() -> Unit>()

  @get:Input
  internal val packageJsonStr: String
    get() = PackageJson("", "") {
      packageJson?.invoke(this) ?: packageJsonSpecs.forEach {
        it()
      }
    }.toString()

  @get:Internal
  var packageJson by project.gradleNullableProperty<(PackageJson.() -> Unit)>()

  /**
   * If set, fully disregards [packageJson] DSL configuration and used the specified raw package.json file as-is.
   */
  @get:InputFile
  @get:Optional
  var packageJsonFile by project.gradleNullableProperty<File>()

  /**
   * DSL builder to configure generated package.json file.
   */
  fun packageJson(config: PackageJson.() -> Unit) {
    packageJsonSpecs.add(config)
  }

  /**
   * DSL Builder to configure the files that compose this publication.
   */
  fun files(config: CopySpec.(destinationDir: File) -> Unit) {
    fileSpecs.add(config)
  }

  /**
   * DSL builder to configure NPM dependencies for this publication.
   */
  fun dependencies(config: MutableList<NpmDependency>.() -> Unit) = npmDependencies.config()
  private fun MutableList<NpmDependency>.dependency(name: String, version: String, scope: NpmDependency.Scope) = NpmDependency(project, name, version, scope, false).also {
    add(it)
  }

  /**
   * Adds a [regular](https://docs.npmjs.com/files/package.json#dependencies) npm dependency.
   */
  fun MutableList<NpmDependency>.npm(name: String, version: String) = dependency(name, version, NpmDependency.Scope.NORMAL)
  /**
   * Adds a [dev](https://docs.npmjs.com/files/package.json#devdependencies) npm dependency.
   */
  fun MutableList<NpmDependency>.npmDev(name: String, version: String) = dependency(name, version, NpmDependency.Scope.DEV)
  /**
   * Adds an [optional](https://docs.npmjs.com/files/package.json#optionaldependencies) npm dependency.
   */
  fun MutableList<NpmDependency>.npmOptional(name: String, version: String) = dependency(name, version, NpmDependency.Scope.OPTIONAL)
  /**
   * Adds a [peer](https://docs.npmjs.com/files/package.json#peerdependencies) npm dependency.
   */
  fun MutableList<NpmDependency>.npmPeer(name: String, version: String) = dependency(name, version, NpmDependency.Scope.PEER)

  internal fun validate(alternativeNodeJsDir: File?): NpmPublication? {
    nodeJsDir = nodeJsDir ?: alternativeNodeJsDir
    return takeIf { nodeJsDir != null }
  }
}
