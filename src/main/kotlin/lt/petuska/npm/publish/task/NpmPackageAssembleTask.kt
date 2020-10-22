package lt.petuska.npm.publish.task

import com.google.gson.Gson
import lt.petuska.npm.publish.dsl.JsonObject
import lt.petuska.npm.publish.dsl.NpmPublication
import lt.petuska.npm.publish.dsl.PackageJson
import lt.petuska.npm.publish.npmPublishing
import lt.petuska.npm.publish.util.fallbackDelegate
import lt.petuska.npm.publish.util.gradleProperty
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmDependency
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import java.io.File
import javax.inject.Inject
import com.google.gson.JsonObject as GsonObject

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
  @get:Nested
  var publication by project.gradleProperty(publication ?: NpmPublication(name, project, project.npmPublishing))

  /**
   * Output directory to assemble the publication to.
   */
  @get:OutputDirectory
  val destinationDir by this.publication.fallbackDelegate(NpmPublication::destinationDir)

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
    with(publication) {
      project.copy { cp ->
        readme?.let { rdm ->
          fileSpecs.add(0) {
            from(rdm)
            rename(rdm.name, "README.md")
          }
        }
        fileSpecs.forEach {
          cp.it(destinationDir)
        }
        cp.into(destinationDir)

        var npmVersion = version
        if (npmVersion.endsWith("-SNAPSHOT")) {
          npmVersion = npmVersion.replace("-SNAPSHOT", "-${System.currentTimeMillis()}")
        }

        packageJsonFile?.let { packageJsonFile ->
          cp.from("$packageJsonFile")
          cp.rename(packageJsonFile.name, "package.json")
        } ?: run {
          PackageJson(moduleName, npmVersion, scope) {
            if (packageJson != null) {
              packageJson!!.invoke(this@PackageJson)
            } else {
              main = this@with.main
              compileKotlinTask?.outputFile?.let {
                val kDir = it.parentFile
                kDir.resolve("${it.nameWithoutExtension}.d.ts").let { dtsFile ->
                  if (dtsFile.exists()) {
                    types = "${dtsFile.relativeTo(kDir)}"
                  }
                }
              }
              npmDependencies.groupBy { dep -> dep.scope }.forEach { (scope, deps) ->
                val dMap = JsonObject<String> {
                  deps.forEach { dep ->
                    dep.name to dep.version
                  }
                }
                when (scope) {
                  NpmDependency.Scope.NORMAL -> this.dependencies = dMap
                  NpmDependency.Scope.DEV -> this.devDependencies = dMap
                  NpmDependency.Scope.OPTIONAL -> this.optionalDependencies = dMap
                  NpmDependency.Scope.PEER -> this.peerDependencies = dMap
                }
              }

              packageJsonSpecs.forEach {
                it()
              }

              if (bundleKotlinDependencies) {
                compileKotlinTask?.bundleKotlinDependencies()?.let { kotlinDependencies ->
                  dependencies {
                    kotlinDependencies.forEach { n, v ->
                      n to v
                    }
                  }
                  val bd = bundledDependencies ?: mutableListOf()
                  bd.addAll(kotlinDependencies.keys)
                  bundledDependencies = bd
                }
              }
            }
          }.writeTo(File(destinationDir, "package.json"))
        }
      }
    }
  }

  private fun Kotlin2JsCompile.bundleKotlinDependencies(): Map<String, String>? = try {
    val gson = Gson()
    val rawPJS = gson.fromJson(destinationDir.resolve("../package.json").readText(), GsonObject::class.java)
    val kotlinDeps = rawPJS["dependencies"]?.asJsonObject?.entrySet()
      ?.map { it.key to it.value.asString }
      ?.filter { it.second.run { startsWith("file:") && contains("packages_imported") } }
      ?.map { (key, value) -> key to File(value.removePrefix("file:")) }

    val targetNodeModulesDir = destinationDir.resolve("node_modules").apply {
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
}
