package lt.petuska.npm.publish.task

import lt.petuska.npm.publish.dsl.NpmPublication
import lt.petuska.npm.publish.dsl.NpmPublishExtension
import lt.petuska.npm.publish.dsl.NpmRepository
import lt.petuska.npm.publish.util.fallbackDelegate
import lt.petuska.npm.publish.util.gradleProperty
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

open class NpmPublishTask @Inject constructor(
  publication: NpmPublication,
  repository: NpmRepository
) : DefaultTask() {
  @get:InputDirectory
  var nodeJsDir by publication.fallbackDelegate(NpmPublication::nodeJsDir)

  @get:Input
  var registry by repository.fallbackDelegate(NpmRepository::registry)

  @get:Input
  var access by repository.fallbackDelegate(NpmRepository::access)

  @get:Input
  @get:Optional
  var authToken by repository.fallbackDelegate(NpmRepository::authToken)

  @get:InputDirectory
  var packageDir by publication.fallbackDelegate(NpmPublication::destinationDir)

  @get:Input
  @get:Optional
  var otp by repository.fallbackDelegate(NpmRepository::otp)

  @get:Input
  var dry by project.gradleProperty(
    (project.properties[NpmPublishExtension.DRY_RUN_PROP] as String?)?.toBoolean()
      ?: false
  )

  init {
    group = "publish"
    description = "Publishes ${publication.name} NPM module to ${repository.name} NPM repository"
  }

  private val npm by lazy {
    if (Os.isFamily(Os.FAMILY_WINDOWS)) {
      nodeJsDir!!
        .resolve("node_modules")
        .resolve("npm")
        .resolve("bin")
        .resolve("npm-cli.js")
    } else {
      nodeJsDir!!
        .resolve("lib")
        .resolve("node_modules")
        .resolve("npm")
        .resolve("bin")
        .resolve("npm-cli.js")
    }
  }

  private val node by lazy {
    // For some unknown reason, the node distribution's structure is different on Windows and UNIX.
    if (Os.isFamily(Os.FAMILY_WINDOWS)) {
      nodeJsDir!!
        .resolve("node.exe")
    } else {
      nodeJsDir!!
        .resolve("bin")
        .resolve("node")
    }
  }

  @TaskAction
  fun doAction() {
    project.exec {
      val cmd = listOfNotNull(
        node,
        npm,
        "publish",
        packageDir,
        "--access $access",
        "--registry=${registry!!.scheme}://${registry!!.authority}/",
        "--//${registry!!.authority}/:_authToken=$authToken",
        if (otp != null) "--otp $otp" else null,
        if (dry) "--dry-run" else ""
      ).toTypedArray()
      it.commandLine(*cmd)
    }
  }
}
