package lt.petuska.npm.publish.task

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
import java.io.File
import javax.inject.Inject

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
    destinationDir.deleteRecursively()
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

        if (packageJsonFile == null) {
          PackageJson(moduleName, npmVersion, scope) {
            if (packageJson != null) {
              packageJson!!.invoke(this@PackageJson)
            } else {
              main = this@with.main
              npmDependencies.groupBy { dep -> dep.scope }.forEach { scope, deps ->
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
            }
          }.writeTo(File(destinationDir, "package.json"))
        } else {
          cp.from("$packageJsonFile")
          cp.rename(packageJsonFile!!.name, "package.json")
        }
      }
    }
  }
}
