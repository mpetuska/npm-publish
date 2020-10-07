package lt.petuska.kpm.publish.task

import lt.petuska.kpm.publish.dsl.KpmPublication
import lt.petuska.kpm.publish.dsl.KpmPublishExtension
import lt.petuska.kpm.publish.util.fallbackDelegate
import lt.petuska.kpm.publish.util.gradleProperty
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsSetupTask
import javax.inject.Inject

open class KpmPublishTask @Inject constructor(
  kpmPublication: KpmPublication
) : DefaultTask() {

  @get:Input
  var registry by kpmPublication.fallbackDelegate(KpmPublication::registry)

  @get:Input
  var access by kpmPublication.fallbackDelegate(KpmPublication::access)

  @get:Input
  @get:Optional
  var authToken by kpmPublication.fallbackDelegate(KpmPublication::authToken)

  @get:InputDirectory
  var packageDir by kpmPublication.fallbackDelegate(KpmPublication::destinationDir)

  @get:Input
  @get:Optional
  var otp by kpmPublication.fallbackDelegate(KpmPublication::otp)

  @get:Input
  var dry by project.gradleProperty(
    (project.properties[KpmPublishExtension.DRY_RUN_PROP] as String?)?.toBoolean()
      ?: false
  )

  private val kotlinNodeJsSetupTask by lazy {
    project.tasks.getByName("kotlinNodeJsSetup") as NodeJsSetupTask
  }

  init {
    group = "publish"
    description = "Publishes ${kpmPublication.name} NPM module"
  }

  private val npm by lazy {
    if (Os.isFamily(Os.FAMILY_WINDOWS)) {
      kotlinNodeJsSetupTask.destination
        .resolve("node_modules")
        .resolve("npm")
        .resolve("bin")
        .resolve("npm-cli.js")
    } else {
      kotlinNodeJsSetupTask.destination
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
      kotlinNodeJsSetupTask.destination
        .resolve("node.exe")
    } else {
      kotlinNodeJsSetupTask.destination
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
        "--registry=${registry.removeSuffix("/")}/",
        "--//${registry.substringAfter("//").removeSuffix("/")}/:_authToken=$authToken",
        if (otp != null) "--otp $otp" else null,
        if (dry) "--dry-run" else ""
      ).toTypedArray()
      it.commandLine(*cmd)
    }
  }
}
