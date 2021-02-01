package lt.petuska.npm.publish.task

import com.google.gson.*
import lt.petuska.npm.publish.*
import lt.petuska.npm.publish.delegate.*
import lt.petuska.npm.publish.dsl.*
import lt.petuska.npm.publish.dsl.JsonObject
import lt.petuska.npm.publish.dsl.PackageJson
import org.gradle.api.*
import org.gradle.api.file.*
import org.gradle.api.tasks.*
import org.jetbrains.kotlin.gradle.targets.js.npm.*
import java.io.*
import javax.inject.*

/**
 * A task to assemble all required files for a given [NpmPublication].
 *
 * @constructor publication to assemble
 */
open class NpmPackageAssembleTask @Inject constructor(
  publication: NpmPublication?
) : DefaultTask() {
  constructor() : this(null)

  /**
   * Main configuration of the publication to assemble.
   * If no publication is passed to a constructor, a default one will be constructed with basic project properties.
   */
  @get:Internal
  var publication by project.gradleProperty(publication ?: NpmPublication(name, project, project.npmPublishing))

  /**
   * Output directory to assemble the publication to.
   */
  @get:Internal
  val destinationDir by this.publication.fallbackDelegate(NpmPublication::destinationDir)

  /**
   * Gson instance to be reused across multiple functions of the task.
   */
  private val gson = Gson()

  init {
    group = "build"
    description = "Assembles ${this.publication.name} NPM publication."
  }

  /**
   * Configuration DSL allowing to modify a given publication config.
   */
  fun publication(config: NpmPublication.() -> Unit) {
    publication.config()
  }

  @TaskAction
  private fun doAction() {
    destinationDir.deleteRecursively()
    with(publication) {
      project.copy { cp ->
        cp.into(this@NpmPackageAssembleTask.destinationDir)
        cp.resolveFiles()

        val kotlinDependencies = kotlinDestinationDir?.copyKotlinDependencies()
        packageJsonFile?.let { packageJsonFile ->
          cp.from("$packageJsonFile")
          cp.rename(packageJsonFile.name, "package.json")
        } ?: resolvePackageJson(kotlinDependencies)
      }
    }
  }

  private fun CopySpec.resolveFiles() = with(publication) {
    readme?.let { rdm ->
      fileSpecs.add(0) {
        from(rdm)
        rename(rdm.name, "README.md")
      }
    }
    fileSpecs.forEach {
      it(destinationDir)
    }
  }

  private fun File.copyKotlinDependencies(): Map<String, String>? = try {
    val pjsFile = this@copyKotlinDependencies.resolve("../package.json").takeIf { it.exists() }
    val rawPJS = gson.fromJson(pjsFile!!.readText(), PackageJson::class.java)
    val kotlinDeps = rawPJS.dependencies
      ?.filter { it.value?.run { startsWith("file:") && contains("packages_imported") } ?: false }
      ?.map { (key, value) -> key to File(value!!.removePrefix("file:")) }

    val targetNodeModulesDir = this@NpmPackageAssembleTask.destinationDir.resolve("node_modules").apply {
      mkdirs()
    }

    kotlinDeps?.forEach { (name, dir) ->
      project.copy { cp ->
        cp.into(targetNodeModulesDir.resolve(name))
        cp.from(dir)
      }
    }
    kotlinDeps?.map { (n, v) -> n to v.name }?.toMap()
  } catch (e: Exception) {
    project.logger.warn("Error preparing node_modules from compilation dependencies.", e)
    null
  }

  private fun resolvePackageJson(kotlinDependencies: Map<String, String>?) = with(publication) {
    var npmVersion = this@with.version
    if (npmVersion?.endsWith("-SNAPSHOT") == true) {
      npmVersion = npmVersion.replace("SNAPSHOT", "${System.currentTimeMillis()}")
    }
    val packageJson = PackageJson(moduleName, npmVersion, scope) {
      if (packageJson != null) {
        packageJson!!.invoke(this@PackageJson)
      } else {
        main = this@with.main
        types = resolveTypes()

        val groupedDependencies = resolveDependencies()
        groupedDependencies.forEach { (scope, deps) ->
          val initialDeps: JsonObject<String> = when (scope) {
            NpmDependency.Scope.NORMAL -> JsonObject<String>().also { dependencies = it }
            NpmDependency.Scope.DEV -> JsonObject<String>().also { devDependencies = it }
            NpmDependency.Scope.OPTIONAL -> JsonObject<String>().also { optionalDependencies = it }
            NpmDependency.Scope.PEER -> JsonObject<String>().also { peerDependencies = it }
          }

          with(initialDeps) {
            deps.forEach { dep ->
              dep.name to dep.version
            }
          }
        }

        bundledDependencies = resolveBundledDependencies(this, kotlinDependencies)

        // Apply overrides from provided template
        publication.packageJsonTemplateFile?.let {
          val template = gson.fromJson(it.readText(), PackageJson::class.java)
          putAll(template)
        }

        packageJsonSpecs.forEach {
          it()
        }
      }
    }.writeTo(File(destinationDir, "package.json"))

    if (publication.shrinkwrapBundledDependencies) {
      packageJson.generateNpmShrinkwrapJson().writeTo(File(destinationDir, "npm-shrinkwrap.json"))
    }
  }

  private fun NpmPublication.resolveDependencies() = npmDependencies.groupBy { dep -> dep.scope }
    .let { deps ->
      val dev = deps[NpmDependency.Scope.DEV]
      val peer = deps[NpmDependency.Scope.PEER]
      val optional = deps[NpmDependency.Scope.OPTIONAL]
      fun NpmDependency.id() = "$scope:$name:$version"
      fun List<NpmDependency>?.includes(other: NpmDependency) = this?.any { it.id() == other.id() } ?: false

      deps.entries.map { (scope, deps) ->
        scope to deps.filter { dep ->
          when (scope) {
            NpmDependency.Scope.NORMAL -> !optional.includes(dep) && !peer.includes(dep) && !dev.includes(dep)
            NpmDependency.Scope.DEV -> !optional.includes(dep) && !peer.includes(dep)
            NpmDependency.Scope.PEER -> !optional.includes(dep)
            NpmDependency.Scope.OPTIONAL -> true
          }
        }
      }
    }

  private fun NpmPublication.resolveTypes() = compileKotlinTask?.outputFile?.let {
    kotlinDestinationDir?.resolve("${it.nameWithoutExtension}.d.ts")?.let { dtsFile ->
      if (dtsFile.exists()) {
        "${dtsFile.relativeTo(dtsFile.parentFile)}"
      } else null
    }
  }

  private fun NpmPublication.resolveBundledDependencies(packageJson: PackageJson, kotlinDependencies: Map<String, String>?): MutableSet<String>? = with(packageJson) {
    (
      bundledDependencies ?: mutableSetOf<String>().also { bd ->
        if (bundleKotlinDependencies) {
          kotlinDependencies?.keys?.let { keys ->
            bd.addAll(keys)
          }
        }
        bundledDependenciesSpec?.applyTo(bd)
      }
      ).takeIf { it.isNotEmpty() }?.also { bd ->
      dependencies {
        kotlinDependencies?.forEach { (n, v) ->
          if (bd.contains(n)) {
            n to v
          }
        }
      }
    }
  }

  private fun PackageJson.generateNpmShrinkwrapJson() = NpmShrinkwrapJson(name!!, version!!) {
    bundledDependencies?.forEach { bundledDependency ->
      this@generateNpmShrinkwrapJson.dependencies?.entries?.find { it.key == bundledDependency }?.let { (npmName, npmVersion) ->
        dependencies {
          dependency(npmName, npmVersion!!, true)
        }
      }
    }
  }
}
