package dev.petuska.npm.publish.task

import com.google.gson.Gson
import dev.petuska.npm.publish.delegate.fallbackDelegate
import dev.petuska.npm.publish.delegate.gradleProperty
import dev.petuska.npm.publish.dsl.JsonObject
import dev.petuska.npm.publish.dsl.NpmPublication
import dev.petuska.npm.publish.dsl.NpmShrinkwrapJson
import dev.petuska.npm.publish.dsl.PackageJson
import dev.petuska.npm.publish.dsl.overrideFrom
import dev.petuska.npm.publish.dsl.writeTo
import dev.petuska.npm.publish.npmPublishing
import org.gradle.api.DefaultTask
import org.gradle.api.file.CopySpec
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.gradle.targets.js.ir.JsIrBinary
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmDependency
import java.io.File
import javax.inject.Inject

/**
 * A task to assemble all required files for a given [NpmPublication].
 *
 * @constructor publication to assemble
 */
open class NpmPackageAssembleTask @Inject constructor(publication: NpmPublication?) :
  DefaultTask() {
  constructor() : this(null)

  /**
   * Main configuration of the publication to assemble. If no publication is passed to a
   * constructor, a default one will be constructed with basic project properties.
   */
  @get:Internal
  var publication by project.gradleProperty(
    publication ?: NpmPublication(name, project, project.npmPublishing)
  )

  /** Output directory to assemble the publication to. */
  @get:Internal
  val destinationDir by this.publication.fallbackDelegate(NpmPublication::destinationDir)

  /** Gson instance to be reused across multiple functions of the task. */
  private val gson = Gson()

  init {
    group = "build"
    description = "Assembles ${this.publication.name} NPM publication."
  }

  /** Configuration DSL allowing to modify a given publication config. */
  fun publication(config: NpmPublication.() -> Unit) {
    publication.config()
  }

  @TaskAction
  private fun doAction() {
    destinationDir.deleteRecursively()
    with(publication) {
      project.copy { cp ->
        cp.duplicatesStrategy = DuplicatesStrategy.WARN
        cp.into(this@NpmPackageAssembleTask.destinationDir)
        cp.resolveFiles()

        val kotlinDependencies = kotlinDestinationDir?.copyKotlinDependencies()
        packageJsonFile?.let { packageJsonFile ->
          cp.from("$packageJsonFile")
          cp.rename(packageJsonFile.name, "package.json")
        }
          ?: resolvePackageJson(kotlinDependencies)
      }
    }
  }

  private fun CopySpec.resolveFiles() =
    with(publication) {
      readme?.let { rdm ->
        fileSpecs.add(0) {
          from(rdm)
          rename(rdm.name, "README.md")
        }
      }
      fileSpecs.forEach { it(destinationDir) }
    }

  private fun File.resolveNpmDependencies(): Map<String, String> =
    try {
      val rawPJS = gson.fromJson(resolve("package.json").readText(), PackageJson::class.java)
      rawPJS
        .dependencies
        ?.mapNotNull { (key, value) ->
          if (value != null && !value.startsWith("file:")) {
            key to value
          } else null
        }
        ?.toMap()
    } catch (e: Exception) {
      project.logger.warn(
        "Error resolving transitive npm dependencies from compilation dependencies.", e
      )
      null
    } ?: mapOf()

  private data class KotlinDependency(
    val name: String,
    val version: String,
    val npmDependencies: Map<String, String>
  )

  private fun File.copyKotlinDependencies(): Map<String, KotlinDependency>? =
    try {
      val pjsFile = this@copyKotlinDependencies.resolve("../package.json").takeIf { it.exists() }
      val rawPJS = gson.fromJson(pjsFile!!.readText(), PackageJson::class.java)
      val kotlinDeps =
        rawPJS.dependencies
          ?.filter {
            it.value?.run { startsWith("file:") && contains("packages_imported") } ?: false
          }
          ?.map { (key, value) -> key to File(value!!.removePrefix("file:")) }

      val targetNodeModulesDir =
        this@NpmPackageAssembleTask.destinationDir.resolve("node_modules").apply { mkdirs() }

      kotlinDeps?.forEach { (name, dir) ->
        project.copy { cp ->
          cp.into(targetNodeModulesDir.resolve(name))
          cp.from(dir)
        }
      }
      kotlinDeps
        ?.map { (n, v) -> n to KotlinDependency(n, v.name, v.resolveNpmDependencies()) }
        ?.toMap()
    } catch (e: Exception) {
      project.logger.warn("Error preparing node_modules from compilation dependencies.", e)
      null
    }

  private fun resolvePackageJson(kotlinDependencies: Map<String, KotlinDependency>?) =
    with(publication) {
      var npmVersion = this@with.version
      if (npmVersion?.endsWith("-SNAPSHOT") == true) {
        npmVersion = npmVersion.replace("SNAPSHOT", "${System.currentTimeMillis()}")
      }
      val packageJson =
        PackageJson(moduleName, npmVersion, scope) {
          if (packageJson != null) {
            packageJson!!.invoke(this@PackageJson)
          } else {
            main = this@with.main
            types = this@with.types ?: resolveTypes()

            if (binary is JsIrBinary) {
              kotlinDependencies
                ?.flatMap {
                  it.value.npmDependencies.map { (name, version) ->
                    NpmDependency(project, name, version)
                  }
                }
                ?.let {
                  val deps = npmDependencies.toSet() + it
                  npmDependencies.clear()
                  npmDependencies.addAll(deps.distinctBy(NpmDependency::key))
                }
            }
            val groupedDependencies = resolveDependencies(kotlinDependencies)
            groupedDependencies.forEach { (scope, deps) ->
              val initialDeps: JsonObject<String> =
                when (scope) {
                  NpmDependency.Scope.NORMAL ->
                    JsonObject<String>().also { dependencies = it }
                  NpmDependency.Scope.DEV ->
                    JsonObject<String>().also { devDependencies = it }
                  NpmDependency.Scope.OPTIONAL ->
                    JsonObject<String>().also { optionalDependencies = it }
                  NpmDependency.Scope.PEER ->
                    JsonObject<String>().also { peerDependencies = it }
                }

              with(initialDeps) { deps.forEach { dep -> dep.name to dep.version } }
            }

            packageJsonSpecs.forEach { it() }
            bundledDependencies = resolveBundledDependencies(this, kotlinDependencies)

            // Apply overrides from provided template
            publication.packageJsonTemplateFile?.let {
              val template = gson.fromJson(it.readText(), PackageJson::class.java)
              overrideFrom(template)
            }

            packageJsonSpecs.forEach { it() }
          }
        }
          .writeTo(File(destinationDir, "package.json"))

      if (publication.shrinkwrapBundledDependencies) {
        packageJson
          .generateNpmShrinkwrapJson()
          ?.writeTo(File(destinationDir, "npm-shrinkwrap.json"))
      }
    }

  private fun NpmPublication.resolveDependencies(
    kotlinDependencies: Map<String, KotlinDependency>?
  ) =
    npmDependencies
      .filter {
        binary !is JsIrBinary ||
          kotlinDependencies?.keys?.let { keys -> it.name !in keys } ?: true
      }
      .groupBy { dep -> dep.scope }
      .let { deps ->
        val dev = deps[NpmDependency.Scope.DEV]
        val peer = deps[NpmDependency.Scope.PEER]
        val optional = deps[NpmDependency.Scope.OPTIONAL]
        fun NpmDependency.id() = "$scope:$name:$version"
        fun List<NpmDependency>?.includes(other: NpmDependency) =
          this?.any { it.id() == other.id() } ?: false

        deps.entries.map { (scope, deps) ->
          scope to
            deps.filter { dep ->
              when (scope) {
                NpmDependency.Scope.NORMAL ->
                  !optional.includes(dep) && !peer.includes(dep) && !dev.includes(dep)
                NpmDependency.Scope.DEV -> !optional.includes(dep) && !peer.includes(dep)
                NpmDependency.Scope.PEER -> !optional.includes(dep)
                NpmDependency.Scope.OPTIONAL -> true
              }
            }
        }
      }

  private fun NpmPublication.resolveTypes() =
    compileKotlinTask?.outputFileProperty?.orNull?.let {
      kotlinDestinationDir?.resolve("${it.nameWithoutExtension}.d.ts")?.let { dtsFile ->
        if (dtsFile.exists()) {
          "${dtsFile.relativeTo(dtsFile.parentFile)}"
        } else null
      }
    }

  private fun NpmPublication.resolveBundledDependencies(
    packageJson: PackageJson,
    kotlinDependencies: Map<String, KotlinDependency>?
  ): MutableSet<String>? =
    with(packageJson) {
      (
        bundledDependencies
          ?: mutableSetOf<String>().also { bd ->
            if (bundleKotlinDependencies) {
              kotlinDependencies?.keys?.let { keys -> bd.addAll(keys) }
            }
            bundledDependenciesSpec?.applyTo(bd)
          }
        )
        .filter {
          binary !is JsIrBinary || kotlinDependencies?.keys?.let { keys -> it !in keys } ?: true
        }
        .toMutableSet()
        .takeIf { it.isNotEmpty() }
        ?.also { bd ->
          dependencies {
            kotlinDependencies?.forEach { (n, v) ->
              if (n in bd) {
                n to v.version
              }
            }
          }
        }
    }

  private fun PackageJson.generateNpmShrinkwrapJson() =
    NpmShrinkwrapJson(name!!, version!!) {
      bundledDependencies?.takeIf { it.isNotEmpty() }?.forEach { bundledDependency ->
        this@generateNpmShrinkwrapJson.dependencies?.entries
          ?.find { it.key == bundledDependency }
          ?.let { (npmName, npmVersion) ->
            dependencies { dependency(npmName, npmVersion!!, true) }
          }
      }
    }
      .takeIf { !it.dependencies.isNullOrEmpty() }
}
