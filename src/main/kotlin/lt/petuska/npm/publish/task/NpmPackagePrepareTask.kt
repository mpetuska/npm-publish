package lt.petuska.npm.publish.task

import lt.petuska.npm.publish.dsl.NpmPublication
import lt.petuska.npm.publish.util.fallbackDelegate
import org.gradle.api.DefaultTask
import org.gradle.api.model.ReplacedBy
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmDependency
import org.jetbrains.kotlin.gradle.targets.js.npm.PackageJson
import java.io.File
import javax.inject.Inject

open class NpmPackagePrepareTask @Inject constructor(
  publication: NpmPublication
) : DefaultTask() {
  private val fileSpecs = publication.fileSpecs
  private val packageJsonOverride = publication.packageJson
  private val packageJsonSpecs = publication.packageJsonSpecs

  @get:InputFile
  @get:Optional
  var readme by publication.fallbackDelegate(NpmPublication::readme)

  @get:Input
  @get:Optional
  var scope by publication.fallbackDelegate(NpmPublication::scope)

  @get:ReplacedBy("npmDependenciesStr")
  var npmDependencies by publication.fallbackDelegate(NpmPublication::npmDependencies)

  @get:Input
  val npmDependenciesStr
    get() = npmDependencies.map {
      it.toString()
    }

  @get:Input
  var main by publication.fallbackDelegate(NpmPublication::main)

  @get:Input
  var packageName by publication.fallbackDelegate(NpmPublication::moduleName)

  @get:Input
  val version = project.version as String

  @get:OutputDirectory
  var destinationDir by publication.fallbackDelegate(NpmPublication::destinationDir)

  init {
    group = "build"
    description = "Assembles ${publication.name} NPM publication."
  }

  @TaskAction
  fun doAction() {
    destinationDir.deleteRecursively()
    project.copy { cp ->
      readme?.let {
        cp.from(it)
        cp.rename(it.name, "README.md")
      }
      fileSpecs.forEach {
        cp.it(destinationDir)
      }
      cp.into(destinationDir)

      var npmVersion = version
      if (npmVersion.endsWith("-SNAPSHOT")) {
        npmVersion = npmVersion.replace("-SNAPSHOT", "-${System.currentTimeMillis()}")
      }

      (scope?.let { PackageJson(it, packageName, npmVersion) } ?: PackageJson(packageName, npmVersion)).apply {
        if (packageJsonOverride != null) {
          packageJsonOverride.invoke(this)
        } else {
          main = this@NpmPackagePrepareTask.main
          npmDependencies.forEach { dep ->
            when (dep.scope) {
              NpmDependency.Scope.NORMAL -> this.dependencies
              NpmDependency.Scope.DEV -> this.devDependencies
              NpmDependency.Scope.OPTIONAL -> this.optionalDependencies
              NpmDependency.Scope.PEER -> this.peerDependencies
            }[dep.name] = dep.version
          }
          packageJsonSpecs.forEach {
            it()
          }
        }
      }.saveTo(File(destinationDir, "package.json"))
//        file("$destinationDir/kotlinx-html-js").renameTo(File("$destinationDir/js-module/kotlinx-html-js"))
    }
  }
}
