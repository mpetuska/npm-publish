package lt.petuska.kpm.publish.task

import lt.petuska.kpm.publish.dsl.KpmPublication
import lt.petuska.kpm.publish.util.fallbackDelegate
import org.gradle.api.DefaultTask
import org.gradle.api.model.ReplacedBy
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmDependency
import org.jetbrains.kotlin.gradle.targets.js.npm.PackageJson
import java.io.File
import javax.inject.Inject

open class KpmPackagePrepareTask @Inject constructor(
  kpmPublication: KpmPublication
) : DefaultTask() {
  @get:InputFile
  @get:Optional
  var readme by kpmPublication.fallbackDelegate(KpmPublication::readme)

  @get:Input
  @get:Optional
  var scope by kpmPublication.fallbackDelegate(KpmPublication::scope)

  @get:Input
  var packageName by kpmPublication.fallbackDelegate(KpmPublication::moduleName)

  @get:Input
  val version = project.version as String

  @get:Input
  val compilationName by lazy {
    compilation!!.name
  }

  @get:Input
  var registry by kpmPublication.fallbackDelegate(KpmPublication::registry)

  @get:ReplacedBy("compilationName")
  var compilation by kpmPublication.fallbackDelegate(KpmPublication::compilation)

  @get:OutputDirectory
  var destinationDir by kpmPublication.fallbackDelegate(KpmPublication::destinationDir)

  init {
    group = "build"
    description = "Assembles ${kpmPublication.name} NPM publication."
    onlyIf {
      compilation != null
    }
  }

  @TaskAction
  fun doAction() {
    project.copy { cp ->
      readme?.let {
        cp.from(it)
        cp.rename(it.name, "README.md")
      }
      cp.from(compilation!!.compileKotlinTask.outputFile.parentFile)
      val processResourcesTask = project.tasks.getByName(compilation!!.processResourcesTaskName) as Copy
      cp.from(processResourcesTask.destinationDir)
      cp.into(destinationDir)

      var npmVersion = version
      if (npmVersion.endsWith("-SNAPSHOT")) {
        npmVersion = npmVersion.replace("-SNAPSHOT", "-${System.currentTimeMillis()}")
      }

      (scope?.let { PackageJson(it, packageName, npmVersion) } ?: PackageJson(packageName, npmVersion)).apply {
        main = compilation!!.compileKotlinTask.outputFile.name
        compilation!!.relatedConfigurationNames.forEach { conf ->
          project.configurations.named(conf).get().dependencies.filterIsInstance<NpmDependency>().forEach { dep ->
            when (dep.scope) {
              NpmDependency.Scope.NORMAL -> this.dependencies
              NpmDependency.Scope.DEV -> this.devDependencies
              NpmDependency.Scope.OPTIONAL -> this.optionalDependencies
              NpmDependency.Scope.PEER -> this.peerDependencies
            }[dep.name] = dep.version
          }
        }
      }.saveTo(File(destinationDir, "package.json"))
//        file("$destinationDir/kotlinx-html-js").renameTo(File("$destinationDir/js-module/kotlinx-html-js"))
    }
  }
}
